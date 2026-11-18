package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger log = LogManager.getLogger(InstructorManager.class);

    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();

    Instructor create(String id, String name, String email) {
        Emails.requireValid(email);
        if (instructors.containsKey(id)) {
            log.warn("Rejected instructor '{}': id already exists", id);
            throw new IllegalArgumentException("An instructor with id '" + id + "' already exists");
        }
        InstructorImpl instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        log.info("Created instructor '{}' ({})", id, name);
        return instructor;
    }

    List<Instructor> getAll() {
        return new ArrayList<>(instructors.values());
    }

    InstructorImpl byId(String id) {
        return instructors.get(id);
    }
}
