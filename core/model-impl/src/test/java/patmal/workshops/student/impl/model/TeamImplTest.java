package patmal.workshops.student.impl.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamImplTest {

    @Test
    void theSameStudentIsNeverAddedTwice() {
        TeamImpl team = new TeamImpl(1, "Alpha", new WorkshopImpl(1, "Java", "basics"));
        StudentImpl alice = new StudentImpl("s1", "Alice", "alice@students.edu");

        team.addStudent(alice);
        team.addStudent(alice);

        assertEquals(1, team.getStudents().size());
    }

    @Test
    void getStudentsReturnsADefensiveCopy() {
        TeamImpl team = new TeamImpl(1, "Alpha", new WorkshopImpl(1, "Java", "basics"));
        team.addStudent(new StudentImpl("s1", "Alice", "alice@students.edu"));

        team.getStudents().clear();

        assertEquals(1, team.getStudents().size());
    }
}
