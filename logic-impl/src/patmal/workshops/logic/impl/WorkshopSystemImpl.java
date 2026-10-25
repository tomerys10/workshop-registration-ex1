package patmal.workshops.logic.impl;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.model.impl.InstructorImpl;
import patmal.workshops.model.impl.StudentImpl;
import patmal.workshops.model.impl.TeamImpl;
import patmal.workshops.model.impl.WorkshopImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In-memory implementation of {@link WorkshopSystem}.
 *
 * <p>Data is kept in maps keyed by id. Workshop and team ids are running
 * numbers starting from 1, each kind with its own counter. A rejected
 * operation never consumes an id and leaves the system unchanged: every
 * request is fully validated before any state is modified.</p>
 *
 * <p>Unknown keys policy (section 3.3): lookups of a single object by an
 * unknown id return {@code null}; list queries for an unknown id return an
 * empty list.</p>
 */
public class WorkshopSystemImpl implements WorkshopSystem {

    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();
    private final Map<String, StudentImpl> students = new LinkedHashMap<>();
    private final Map<Integer, WorkshopImpl> workshops = new LinkedHashMap<>();
    private final Map<Integer, TeamImpl> teams = new LinkedHashMap<>();

    private int nextWorkshopId = 1;
    private int nextTeamId = 1;

    public WorkshopSystemImpl() {
        // Public no-argument constructor, as required by the tester.
    }

    // ------------------------------------------------------------------
    // Creation
    // ------------------------------------------------------------------

    @Override
    public Instructor createInstructor(String id, String name, String email) {
        if (instructors.containsKey(id)) {
            throw new IllegalArgumentException("An instructor with id '" + id + "' already exists");
        }
        InstructorImpl instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        return instructor;
    }

    @Override
    public Student createStudent(String id, String name, String email) {
        if (students.containsKey(id)) {
            throw new IllegalArgumentException("A student with id '" + id + "' already exists");
        }
        StudentImpl student = new StudentImpl(id, name, email);
        students.put(id, student);
        return student;
    }

    @Override
    public Workshop createWorkshop(String name, String description) {
        WorkshopImpl workshop = new WorkshopImpl(nextWorkshopId, name, description);
        workshops.put(nextWorkshopId, workshop);
        nextWorkshopId++;
        return workshop;
    }

    @Override
    public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        InstructorImpl instructorImpl = (InstructorImpl) instructor;
        WorkshopImpl workshopImpl = (WorkshopImpl) workshop;

        // Keep both sides of the relation consistent. If the workshop was
        // previously associated with another instructor, detach it first.
        Instructor previous = workshopImpl.getInstructor();
        if (previous instanceof InstructorImpl && previous != instructorImpl) {
            ((InstructorImpl) previous).removeWorkshop(workshopImpl);
        }
        workshopImpl.setInstructor(instructorImpl);
        instructorImpl.addWorkshop(workshopImpl);
    }

    @Override
    public Team createTeam(String name, Workshop workshop, List<String> members) {
        // Validate the whole request before changing anything, so a rejected
        // operation leaves the system (data and id counters) untouched.
        Set<String> seen = new HashSet<>();
        List<StudentImpl> resolved = new ArrayList<>();
        for (String memberId : members) {
            if (!seen.add(memberId)) {
                throw new IllegalArgumentException(
                        "Student '" + memberId + "' is listed more than once");
            }
            StudentImpl student = students.get(memberId);
            if (student == null) {
                throw new IllegalArgumentException(
                        "Student '" + memberId + "' does not exist");
            }
            if (student.getTeam() != null) {
                throw new IllegalArgumentException(
                        "Student '" + memberId + "' is already in team '"
                                + student.getTeam().getName() + "'");
            }
            resolved.add(student);
        }

        TeamImpl team = new TeamImpl(nextTeamId, name, workshop);
        for (StudentImpl student : resolved) {
            team.addStudent(student);
            student.setTeam(team);
        }
        teams.put(nextTeamId, team);
        nextTeamId++;
        return team;
    }

    // ------------------------------------------------------------------
    // Queries
    // ------------------------------------------------------------------

    @Override
    public List<Instructor> getInstructors() {
        return new ArrayList<>(instructors.values());
    }

    @Override
    public Instructor getInstructor(String id) {
        return instructors.get(id); // null when unknown (see class doc)
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>(students.values());
    }

    @Override
    public Student getStudent(String id) {
        return students.get(id); // null when unknown (see class doc)
    }

    @Override
    public List<Workshop> getWorkshops() {
        return new ArrayList<>(workshops.values());
    }

    @Override
    public Workshop getWorkshop(Integer id) {
        return workshops.get(id); // null when unknown (see class doc)
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    @Override
    public Team getTeam(Integer id) {
        return teams.get(id); // null when unknown (see class doc)
    }

    @Override
    public List<Student> getFreeStudents() {
        List<Student> free = new ArrayList<>();
        for (StudentImpl student : students.values()) {
            if (student.getTeam() == null) {
                free.add(student);
            }
        }
        return free;
    }

    @Override
    public List<Student> getStudentsOfInstructor(String instructorId) {
        List<Student> result = new ArrayList<>();
        InstructorImpl instructor = instructors.get(instructorId);
        if (instructor == null) {
            return result; // empty list when unknown (see class doc)
        }
        for (TeamImpl team : teams.values()) {
            Workshop workshop = team.getWorkshop();
            if (workshop != null && workshop.getInstructor() == instructor) {
                result.addAll(team.getStudents());
            }
        }
        return result;
    }

    @Override
    public List<Student> getStudentsOfWorkshop(Integer workshopId) {
        List<Student> result = new ArrayList<>();
        WorkshopImpl workshop = workshops.get(workshopId);
        if (workshop == null) {
            return result; // empty list when unknown (see class doc)
        }
        for (TeamImpl team : teams.values()) {
            if (team.getWorkshop() == workshop) {
                result.addAll(team.getStudents());
            }
        }
        return result;
    }
}
