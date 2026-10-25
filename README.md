# Workshop Registration System – Exercise 1 (Vanilla Java)

An in-memory core implementation of the **Workshop Registration System**, developed in plain Vanilla Java (JDK 25) against the provided `model` and `logic` API interfaces.

No build tools (Maven / Gradle) and no frameworks are used – only the standard JDK (`javac`, `jar`, `java`).

---

## 1. Overview & Architecture

The system manages registrations for academic/training workshops. It tracks **Instructors**, the **Workshops** they teach, **Students**, and the **Teams** formed by students to participate in workshops.

```text
Instructor 1 ──── * Workshop 1 ──── * Team 1 ──── 1..* Student
```

* An instructor teaches zero or more workshops.
* A workshop belongs to at most one instructor and hosts any number of teams.
* A team belongs to exactly one workshop and contains one or more students.
* A student belongs to at most one team. A student without a team is a **free student**.

Because every team belongs to a workshop and every workshop belongs to an instructor, the system can answer, for any student, which team they are in, who their team mates are, and who their instructor is.

The project strictly separates the provided API interfaces from the concrete implementation and is organized into two main layers under the `patmal.workshops` namespace. The given interfaces are consumed as-is from the API jars and are never modified.

* **Model Layer (`patmal.workshops.model.impl`)**:
  * `PersonBase`: Abstract base class encapsulating common identifying attributes (`id`, `name`, `email`) for all persons in the system. The fields are `final`, since a person's identity never changes after creation.
  * `InstructorImpl`: Implements `Instructor`. Maintains the collection of workshops taught by the instructor.
  * `StudentImpl`: Implements `Student`. Holds a reference to the student's `Team` (`null` for free students) and dynamically derives `getTeamName()`, `getInstructor()`, and `getTeamMates()` (excluding the student himself/herself) directly from the associated team and workshop.
  * `WorkshopImpl`: Implements `Workshop`. Identified by a sequential system-generated ID starting from `1`. Its instructor is `null` until the workshop is associated with one.
  * `TeamImpl`: Implements `Team`. Identified by an independent sequential system-generated ID starting from `1`, linking a group of students to a specific workshop.

* **Logic Layer (`patmal.workshops.logic.impl`)**:
  * `WorkshopSystemImpl`: Implements `WorkshopSystem` and serves as the single entry point and in-memory registry for creating entities, establishing relationships, and querying system state. It is `public` and exposes a `public` no-argument constructor, so the tester can discover and instantiate it automatically from the classpath.

The model layer depends only on the API interfaces. The logic layer depends on the API interfaces and on the model layer. This mirrors the two-module structure recommended by the exercise, and keeps the door open for future evolution of the project (e.g. replacing the in-memory registry with persistent storage without touching the model).

---

## 2. Key Design Decisions & Considerations

### 2.1 Data Structures & Lookup Efficiency

* Entities (`Instructor`, `Student`, `Workshop`, `Team`) are stored in four `LinkedHashMap` instances keyed by their unique IDs (`String` for persons, `Integer` for workshops and teams).
* This provides $O(1)$ lookup by ID, which is the most frequent operation in the system (every `getX(id)` call and every member of a `createTeam` request).
* `LinkedHashMap` was chosen over `HashMap` to preserve insertion order. The API states that the order of returned lists is not important, but deterministic ordering makes the output predictable and debugging considerably easier.

### 2.2 Identifiers

* **Instructor and student IDs** are supplied by the caller and must be unique within their kind. Uniqueness is enforced on creation; a duplicate ID is rejected with an `IllegalArgumentException`.
* **Workshop and team IDs** are assigned by the system from two independent counters (`nextWorkshopId`, `nextTeamId`), both starting from `1` and incremented in creation order.
* A counter is incremented **only after** an operation has fully succeeded. A rejected operation therefore never consumes an ID, and the next successful creation receives the next number in the sequence.

### 2.3 Atomicity & Two-Phase Validation

* Every mutating operation that can fail (`createInstructor`, `createStudent`, `createTeam`) validates all preconditions *before* modifying any system state or incrementing internal ID counters.
* `createTeam` is the most delicate operation and is explicitly split into two phases:
  1. **Validation phase** – every member ID is checked for:
     * **existence** – the student must be registered in the system;
     * **uniqueness within the request** – the same ID may not appear twice (tracked with a `HashSet`);
     * **availability** – the student must currently be a free student.

     The resolved students are collected into a temporary list; nothing in the system is touched yet.
  2. **Commit phase** – only when every member passed validation is the team created, its students linked, the team registered, and the team counter advanced.
* If any check fails, an `IllegalArgumentException` with a descriptive message is thrown during the validation phase, leaving all existing entities and sequential ID counters completely untouched. For example, `createTeam("X", w, ["s7", "s99"])` where `s99` does not exist leaves `s7` a free student, exactly as before the call.

### 2.4 Bidirectional Relationship Consistency

Every relation in the model is navigable from both sides, so both sides must always agree.

* **Instructor ↔ Workshop**: `associateWorkshopWithInstructor` updates both `WorkshopImpl#setInstructor` and `InstructorImpl#addWorkshop`. If a workshop was previously associated with another instructor, it is cleanly detached from the previous instructor first, so a workshop never appears in two instructors' lists.
* **Team ↔ Student**: `createTeam` updates both `TeamImpl#addStudent` and `StudentImpl#setTeam`.
* **Derived Student State**: Rather than caching redundant fields on `StudentImpl`, student properties (`teamName`, `instructor`, `teamMates`) are computed on the fly from the single `Team` reference:
  * `getTeamName()` → `team.getName()`
  * `getInstructor()` → `team.getWorkshop().getInstructor()`
  * `getTeamMates()` → `team.getStudents()` without the student himself/herself

  Each fact is stored in exactly one place, which guarantees zero state-synchronization bugs as the system evolves. For instance, when a workshop is associated with an instructor *after* teams were already formed, every student of that workshop immediately reports the correct instructor with no extra bookkeeping.
* Mutators such as `addWorkshop` and `addStudent` ignore an object that is already present, so internal lists never contain the same object twice.

### 2.5 Encapsulation & Defensive Copying

* All methods returning collections (`getWorkshops()`, `getStudents()`, `getTeamMates()`, and all system-level queries) return fresh defensive copies (`new ArrayList<>(...)`), preventing external callers from mutating internal collections.
* Returned lists are never `null` – an empty result is always an empty list.
* Mutating methods on model classes (`setInstructor`, `addWorkshop`, `addStudent`, `setTeam`) are intended to be called only by the logic layer, which is responsible for keeping both sides of every relation consistent.

### 2.6 Derived Queries

* `getFreeStudents()` returns every student whose team reference is `null`.
* `getStudentsOfWorkshop(id)` collects the students of every team whose workshop is the requested one.
* `getStudentsOfInstructor(id)` collects the students of every team whose workshop is taught by the requested instructor. An instructor who teaches several workshops therefore receives the students of all of them, and since a student belongs to at most one team, no student is ever reported twice.

### 2.7 Unknown Keys Policy (Section 3.3)

The API leaves the behaviour for unknown IDs open, as long as it is consistent and documented. The chosen policy is:

* Single-entity lookups by an unknown ID (`getInstructor(id)`, `getStudent(id)`, `getWorkshop(id)`, `getTeam(id)`) consistently return `null`.
* Collection queries by an unknown ID (`getStudentsOfInstructor(id)`, `getStudentsOfWorkshop(id)`) consistently return an empty list (`[]`).

This keeps lookups cheap and side-effect free, and keeps collection queries safe to iterate without a `null` check. The policy is also documented in the Javadoc of `WorkshopSystemImpl`.

### 2.8 Invalid Operations

The following operations are rejected by throwing an `IllegalArgumentException`:

| Operation | Rejected when |
|---|---|
| `createInstructor` | an instructor with the same ID already exists |
| `createStudent` | a student with the same ID already exists |
| `createTeam` | a listed student ID does not exist |
| `createTeam` | a listed student is already in another team |
| `createTeam` | the same student is listed twice |

---

## 3. Repository Structure

```text
.
├── artifacts/                  # Compiled implementation JARs for automated grading
│   ├── model-impl-1.0.jar
│   └── logic-impl-1.0.jar
├── lib/                        # Provided API JARs (interfaces only)
│   ├── model-1.0.jar
│   └── logic-1.0.jar
├── model-impl/                 # Model implementation module (patmal.workshops.model.impl)
│   └── src/patmal/workshops/model/impl/
│       ├── PersonBase.java
│       ├── InstructorImpl.java
│       ├── StudentImpl.java
│       ├── WorkshopImpl.java
│       └── TeamImpl.java
├── logic-impl/                 # Logic implementation module (patmal.workshops.logic.impl)
│   └── src/patmal/workshops/logic/impl/
│       └── WorkshopSystemImpl.java
├── build.ps1                   # Compiles both modules and packages the JARs into artifacts/
└── README.md
```

---

## 4. Building

Requirements: JDK 25 with `javac` and `jar` on the `PATH`.

```powershell
.\build.ps1
```

The script:

1. compiles `model-impl` against the API jars;
2. compiles `logic-impl` against the API jars and the compiled model classes;
3. packages each module into its own jar: `artifacts\model-impl-1.0.jar` and `artifacts\logic-impl-1.0.jar`.

The modules are compiled in this order because the logic layer depends on the model layer.

---

## 5. Running the Tester

The tester (`workshop-tester.jar`) is a black-box program that knows only the API. It searches the classpath for exactly one public, concrete implementation of `WorkshopSystem` with a public no-argument constructor, creates a fresh instance for every flow, and compares the results with the expected ones.

Run from the repository root, with `workshop-tester.jar` placed next to it (on Windows, classpath entries are separated with `;`):

```powershell
java -cp "workshop-tester.jar;lib\logic-1.0.jar;lib\model-1.0.jar;artifacts\model-impl-1.0.jar;artifacts\logic-impl-1.0.jar" patmal.workshops.main.Main
```

### Result

All nine flows pass:

| # | Flow | Checks |
|---|---|---|
| 1 | Instructors, students, workshops and teams are registered | 23/23 |
| 2 | Workshops and instructors are associated | 8/8 |
| 3 | Team composition and derived student data | 20/20 |
| 4 | Free students | 2/2 |
| 5 | Students of an instructor | 4/4 |
| 6 | Students of a workshop | 4/4 |
| 7 | Unknown keys | 6/6 |
| 8 | Invalid operations are rejected and leave the system intact | 13/13 |
| 9 | System evolves: new team, workshop, student | 25/25 |

```text
Scenarios: 9 passed, 0 failed
Checks:    105 passed, 0 failed
RESULT: ALL OK
```

---

## 6. Submission

* The repository is public.
* The implementation jars are located in the `artifacts` folder at the repository root.
* The submitted commit is tagged `EX1-Vanilla`.
