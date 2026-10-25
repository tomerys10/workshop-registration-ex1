# Workshop Registration System (EX1 - Vanilla)

An in-memory implementation of the workshop registration API: instructors,
workshops, students and the teams students form to take part in a workshop.

Plain vanilla Java (JDK 25). No Maven, no frameworks.

## Structure

| Folder | Content |
|---|---|
| `model-impl` | Implementations of the model interfaces (`Person`, `Instructor`, `Student`, `Workshop`, `Team`) |
| `logic-impl` | Implementation of `WorkshopSystem` (the single entry point) |
| `lib` | The given API jars (`model-1.0.jar`, `logic-1.0.jar`) |
| `artifacts` | The built implementation jars |

## Design notes

- Data is kept in maps keyed by id. Workshop and team ids are running numbers
  starting from 1, each kind with its own counter.
- Every request is fully validated before any state changes, so a rejected
  operation leaves the system (data and id counters) exactly as it was.
- All returned lists are defensive copies of the internal state.
- Unknown keys: `getX(id)` returns `null`, `getStudentsOfX(id)` returns an
  empty list.

## Build

```powershell
.\build.ps1
```

This compiles both modules and packages `artifacts\model-impl-1.0.jar` and
`artifacts\logic-impl-1.0.jar`.

## Run the tester

From the folder that holds `workshop-tester.jar`:

```powershell
java -cp "workshop-tester.jar;lib\logic-1.0.jar;lib\model-1.0.jar;artifacts\model-impl-1.0.jar;artifacts\logic-impl-1.0.jar" patmal.workshops.main.Main
```

Expected output ends with `RESULT: ALL OK`.
