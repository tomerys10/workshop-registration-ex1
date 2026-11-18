package patmal.workshops.student.impl.logic;

import patmal.workshops.api.model.Student;
import patmal.workshops.student.impl.model.StudentImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the students: creation, uniqueness of the id, lookups and the
 * free-students query.
 */
class StudentManager {

    private final Map<String, StudentImpl> students = new LinkedHashMap<>();

    Student create(String id, String name, String email) {
        if (students.containsKey(id)) {
            throw new IllegalArgumentException("A student with id '" + id + "' already exists");
        }
        StudentImpl student = new StudentImpl(id, name, email);
        students.put(id, student);
        return student;
    }

    List<Student> getAll() {
        return new ArrayList<>(students.values());
    }

    StudentImpl byId(String id) {
        return students.get(id);
    }

    List<Student> getFree() {
        List<Student> free = new ArrayList<>();
        for (StudentImpl student : students.values()) {
            if (student.getTeam() == null) {
                free.add(student);
            }
        }
        return free;
    }
}
