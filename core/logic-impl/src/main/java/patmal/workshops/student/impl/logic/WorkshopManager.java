package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.model.InstructorImpl;
import patmal.workshops.student.impl.model.WorkshopImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the workshops: the running id, creation, lookups and the association
 * between a workshop and its instructor.
 */
class WorkshopManager {

    private static final Logger log = LogManager.getLogger(WorkshopManager.class);

    private final Map<Integer, WorkshopImpl> workshops = new LinkedHashMap<>();
    private int nextWorkshopId = 1;

    Workshop create(String name, String description) {
        WorkshopImpl workshop = new WorkshopImpl(nextWorkshopId, name, description);
        workshops.put(nextWorkshopId, workshop);
        nextWorkshopId++;
        log.info("Created workshop {} ('{}')", workshop.getId(), name);
        return workshop;
    }

    List<Workshop> getAll() {
        return new ArrayList<>(workshops.values());
    }

    WorkshopImpl byId(Integer id) {
        return workshops.get(id);
    }

    void associateWithInstructor(Instructor instructor, Workshop workshop) {
        InstructorImpl instructorImpl = (InstructorImpl) instructor;
        WorkshopImpl workshopImpl = (WorkshopImpl) workshop;

        Instructor previous = workshopImpl.getInstructor();
        if (previous instanceof InstructorImpl && previous != instructorImpl) {
            ((InstructorImpl) previous).removeWorkshop(workshopImpl);
        }
        workshopImpl.setInstructor(instructorImpl);
        instructorImpl.addWorkshop(workshopImpl);
        log.info("Associated workshop {} with instructor '{}'", workshopImpl.getId(), instructorImpl.getId());
    }
}
