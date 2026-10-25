package patmal.workshops.model.impl;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;

import java.util.ArrayList;
import java.util.List;

public class StudentImpl implements Student {

    private final String id;
    private final String name;
    private final String email;
    private Team team;

    public StudentImpl(String id, String name, String email) {
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
    public String getTeamName() {
        return team == null ? null : team.getName();
    }

    @Override
    public Instructor getInstructor() {
        if (team == null || team.getWorkshop() == null) {
            return null;
        }
        return team.getWorkshop().getInstructor();
    }

    @Override
    public List<Student> getTeamMates() {
        if (team == null) {
            return new ArrayList<>();
        }
        List<Student> mates = new ArrayList<>(team.getStudents());
        mates.remove(this);
        return mates;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return id;
    }
}
