package patmal.workshops.model.impl;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * A student. Most of the student's information (team name, instructor,
 * team mates) is derived from the team the student belongs to.
 * A student without a team is a "free" student: team name and instructor
 * are null and the list of team mates is empty.
 */
public class StudentImpl extends PersonBase implements Student {

    private Team team;

    public StudentImpl(String id, String name, String email) {
        super(id, name, email);
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
        mates.remove(this); // the student himself/herself is not a team mate
        return mates;
    }

    /**
     * Returns the team this student belongs to, or null for a free student.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Puts this student in a team.
     * Called by the logic layer, which also updates the team side of the relation.
     */
    public void setTeam(Team team) {
        this.team = team;
    }
}
