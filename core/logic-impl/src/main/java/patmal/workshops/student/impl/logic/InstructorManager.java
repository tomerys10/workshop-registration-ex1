package patmal.workshops.student.impl.logic;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.student.impl.model.InstructorImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the instructors: creation, uniqueness of the id and lookups.
 */
class InstructorManager {

    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();

    Instructor create(String id, String name, String email) {
        if (instructors.containsKey(id)) {
            throw new IllegalArgumentException("An instructor with id '" + id + "' already exists");
        }
        InstructorImpl instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        return instructor;
    }

    List<Instructor> getAll() {
        return new ArrayList<>(instructors.values());
    }

    InstructorImpl byId(String id) {
        return instructors.get(id);
    }
}
