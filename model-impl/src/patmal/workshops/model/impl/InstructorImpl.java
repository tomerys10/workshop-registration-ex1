package patmal.workshops.model.impl;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

/**
 * An instructor that teaches zero or more workshops.
 * The list of workshops is kept consistent with {@link WorkshopImpl#getInstructor()}
 * by the logic layer when a workshop is associated with an instructor.
 */
public class InstructorImpl extends PersonBase implements Instructor {

    private final List<Workshop> workshops = new ArrayList<>();

    public InstructorImpl(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public List<Workshop> getWorkshops() {
        // Defensive copy so callers cannot modify the internal state.
        return new ArrayList<>(workshops);
    }

    /**
     * Adds a workshop to the list of workshops this instructor teaches.
     * Called by the logic layer when the relation is created.
     */
    public void addWorkshop(Workshop workshop) {
        if (!workshops.contains(workshop)) {
            workshops.add(workshop);
        }
    }

    /**
     * Removes a workshop from this instructor (used when a workshop is re-associated).
     */
    public void removeWorkshop(Workshop workshop) {
        workshops.remove(workshop);
    }
}
