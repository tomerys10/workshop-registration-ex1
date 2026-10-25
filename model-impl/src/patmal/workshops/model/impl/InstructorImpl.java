package patmal.workshops.model.impl;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

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
        return new ArrayList<>(workshops);
    }

    public void addWorkshop(Workshop workshop) {
        if (!workshops.contains(workshop)) {
            workshops.add(workshop);
        }
    }

    public void removeWorkshop(Workshop workshop) {
        workshops.remove(workshop);
    }

    @Override
    public String toString() {
        return id;
    }
}
