package patmal.workshops.student.impl.logic;

import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.model.InstructorImpl;
import patmal.workshops.student.impl.model.StudentImpl;
import patmal.workshops.student.impl.model.TeamImpl;
import patmal.workshops.student.impl.model.WorkshopImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Owns the teams: the running id, creation (with full validation before any
 * state change) and the queries that walk over the teams.
 */
class TeamManager {

    private final Map<Integer, TeamImpl> teams = new LinkedHashMap<>();
    private final StudentManager studentManager;
    private int nextTeamId = 1;

    TeamManager(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    Team create(String name, Workshop workshop, List<String> members) {
        Set<String> seen = new HashSet<>();
        List<StudentImpl> resolved = new ArrayList<>();
        for (String memberId : members) {
            if (!seen.add(memberId)) {
                throw new IllegalArgumentException(
                        "Student '" + memberId + "' is listed more than once");
            }
            StudentImpl student = studentManager.byId(memberId);
            if (student == null) {
                throw new IllegalArgumentException(
                        "Student '" + memberId + "' does not exist");
            }
            if (student.getTeam() != null) {
                throw new IllegalArgumentException(
                        "Student '" + memberId + "' is already in team '"
                                + student.getTeam().getName() + "'");
            }
            resolved.add(student);
        }

        TeamImpl team = new TeamImpl(nextTeamId, name, workshop);
        for (StudentImpl student : resolved) {
            team.addStudent(student);
            student.setTeam(team);
        }
        teams.put(nextTeamId, team);
        nextTeamId++;
        return team;
    }

    List<Team> getAll() {
        return new ArrayList<>(teams.values());
    }

    TeamImpl byId(Integer id) {
        return teams.get(id);
    }

    List<Student> getStudentsOfInstructor(InstructorImpl instructor) {
        List<Student> result = new ArrayList<>();
        if (instructor == null) {
            return result;
        }
        for (TeamImpl team : teams.values()) {
            Workshop workshop = team.getWorkshop();
            if (workshop != null && workshop.getInstructor() == instructor) {
                result.addAll(team.getStudents());
            }
        }
        return result;
    }

    List<Student> getStudentsOfWorkshop(WorkshopImpl workshop) {
        List<Student> result = new ArrayList<>();
        if (workshop == null) {
            return result;
        }
        for (TeamImpl team : teams.values()) {
            if (workshop.equals(team.getWorkshop())) {
                result.addAll(team.getStudents());
            }
        }
        return result;
    }
}
