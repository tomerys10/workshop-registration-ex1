package patmal.workshops.student.impl.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The system is exercised only through the WorkshopSystem interface, exactly
 * like the tester does. Every rejected operation must leave the system as it
 * was.
 */
class WorkshopSystemImplTest {

    private WorkshopSystem system;

    @BeforeEach
    void setUp() {
        system = new WorkshopSystemImpl();
    }

    @Test
    void duplicateInstructorIdIsRejectedAndNothingChanges() {
        system.createInstructor("i1", "Dana", "dana@univ.edu");

        assertThrows(IllegalArgumentException.class,
                () -> system.createInstructor("i1", "Someone Else", "other@univ.edu"));

        assertEquals(1, system.getInstructors().size());
        assertEquals("Dana", system.getInstructor("i1").getName());
    }

    @Test
    void invalidEmailIsRejectedAndTheIdStaysUsable() {
        assertThrows(IllegalArgumentException.class,
                () -> system.createStudent("s1", "Alice", "not-an-email"));
        assertThrows(IllegalArgumentException.class,
                () -> system.createStudent("s1", "Alice", null));
        assertTrue(system.getStudents().isEmpty());

        // the rejected id is still free for a valid creation
        system.createStudent("s1", "Alice", "alice@students.edu");
        assertEquals(1, system.getStudents().size());
    }

    @Test
    void teamWithUnknownMemberIsRejectedAtomically() {
        Workshop workshop = system.createWorkshop("Java", "basics");
        system.createStudent("s1", "Alice", "alice@students.edu");

        assertThrows(IllegalArgumentException.class,
                () -> system.createTeam("Alpha", workshop, List.of("s1", "s99")));

        // nothing changed: no team, and s1 is still free
        assertTrue(system.getTeams().isEmpty());
        assertEquals(1, system.getFreeStudents().size());

        // the rejected call did not consume a team id
        Team team = system.createTeam("Alpha", workshop, List.of("s1"));
        assertEquals(1, team.getId());
    }

    @Test
    void reassociatingAWorkshopRemovesItFromThePreviousInstructor() {
        Instructor first = system.createInstructor("i1", "Dana", "dana@univ.edu");
        Instructor second = system.createInstructor("i2", "Noa", "noa@univ.edu");
        Workshop workshop = system.createWorkshop("Java", "basics");

        system.associateWorkshopWithInstructor(first, workshop);
        system.associateWorkshopWithInstructor(second, workshop);

        assertTrue(first.getWorkshops().isEmpty());
        assertEquals(1, second.getWorkshops().size());
        assertEquals(second, workshop.getInstructor());
    }

    @Test
    void associatingTheSameWorkshopTwiceDoesNotDuplicateIt() {
        Instructor dana = system.createInstructor("i1", "Dana", "dana@univ.edu");
        Workshop workshop = system.createWorkshop("Java", "basics");

        system.associateWorkshopWithInstructor(dana, workshop);
        system.associateWorkshopWithInstructor(dana, workshop);

        assertEquals(1, dana.getWorkshops().size());
    }

    @Test
    void studentsOfWorkshopAreCollectedFromItsTeams() {
        Workshop java = system.createWorkshop("Java", "basics");
        Workshop spring = system.createWorkshop("Spring", "advanced");
        system.createStudent("s1", "Alice", "alice@students.edu");
        system.createStudent("s2", "Bob", "bob@students.edu");
        system.createStudent("s3", "Carol", "carol@students.edu");
        system.createTeam("Alpha", java, List.of("s1", "s2"));
        system.createTeam("Beta", spring, List.of("s3"));

        List<Student> ofJava = system.getStudentsOfWorkshop(java.getId());

        assertEquals(2, ofJava.size());
        assertTrue(system.getStudentsOfWorkshop(99).isEmpty());
    }
}
