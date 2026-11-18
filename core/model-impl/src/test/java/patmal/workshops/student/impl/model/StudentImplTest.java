package patmal.workshops.student.impl.model;

import org.junit.jupiter.api.Test;
import patmal.workshops.api.model.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentImplTest {

    @Test
    void freeStudentHasNoTeamNoInstructorAndNoTeamMates() {
        StudentImpl free = new StudentImpl("s1", "Alice", "alice@students.edu");

        assertNull(free.getTeamName());
        assertNull(free.getInstructor());
        assertTrue(free.getTeamMates().isEmpty());
    }

    @Test
    void teamMatesNeverContainTheStudentHimself() {
        WorkshopImpl workshop = new WorkshopImpl(1, "Java", "basics");
        TeamImpl team = new TeamImpl(1, "Alpha", workshop);
        StudentImpl alice = new StudentImpl("s1", "Alice", "alice@students.edu");
        StudentImpl bob = new StudentImpl("s2", "Bob", "bob@students.edu");
        team.addStudent(alice);
        team.addStudent(bob);
        alice.setTeam(team);
        bob.setTeam(team);

        List<Student> mates = alice.getTeamMates();

        assertEquals(1, mates.size());
        assertFalse(mates.contains(alice));
        assertTrue(mates.contains(bob));
    }

    @Test
    void instructorIsDerivedThroughTeamAndWorkshop() {
        InstructorImpl dana = new InstructorImpl("i1", "Dana", "dana@univ.edu");
        WorkshopImpl workshop = new WorkshopImpl(1, "Java", "basics");
        workshop.setInstructor(dana);
        TeamImpl team = new TeamImpl(1, "Alpha", workshop);
        StudentImpl alice = new StudentImpl("s1", "Alice", "alice@students.edu");
        team.addStudent(alice);
        alice.setTeam(team);

        assertEquals(dana, alice.getInstructor());
        assertEquals("Alpha", alice.getTeamName());
    }
}
