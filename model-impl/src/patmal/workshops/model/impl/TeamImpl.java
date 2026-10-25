package patmal.workshops.model.impl;

import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of students that take a specific workshop together.
 * The id is assigned by the system (a running number starting from 1).
 */
public class TeamImpl implements Team {

    private final Integer id;
    private final String name;
    private final Workshop workshop;
    private final List<Student> students = new ArrayList<>();

    public TeamImpl(Integer id, String name, Workshop workshop) {
        this.id = id;
        this.name = name;
        this.workshop = workshop;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Student> getStudents() {
        // Defensive copy so callers cannot modify the internal state.
        return new ArrayList<>(students);
    }

    @Override
    public Workshop getWorkshop() {
        return workshop;
    }

    /**
     * Adds a student to this team.
     * Called by the logic layer, which also updates the student side of the relation.
     */
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
