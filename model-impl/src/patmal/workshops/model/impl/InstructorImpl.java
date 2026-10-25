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
public class InstructorImpl implements Instructor {

    private final String id;
    private final String name;
    private final String email;
    private final List<Workshop> workshops = new ArrayList<>();

    public InstructorImpl(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
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

    @Override
    public String toString() {
        return id;
    }
}
