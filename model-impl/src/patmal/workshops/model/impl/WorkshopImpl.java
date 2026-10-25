package patmal.workshops.model.impl;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

/**
 * A workshop that teams register to.
 * The id is assigned by the system (a running number starting from 1).
 * The instructor is null until the workshop is associated with one.
 */
public class WorkshopImpl implements Workshop {

    private final Integer id;
    private final String name;
    private final String description;
    private Instructor instructor;

    public WorkshopImpl(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
    public String getDescription() {
        return description;
    }

    @Override
    public Instructor getInstructor() {
        return instructor;
    }

    /**
     * Sets the instructor that teaches this workshop.
     * Called by the logic layer, which also updates the instructor side of the relation.
     */
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return id + " " + name;
    }
}
