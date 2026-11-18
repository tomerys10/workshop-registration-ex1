package patmal.workshops.student.impl.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Workshops are "the same workshop" when they have the same id: equals and
 * hashCode are based on the id only, so collections (contains, remove) find a
 * workshop by its identity in the system, not by its address in memory.
 */
class WorkshopImplTest {

    @Test
    void workshopsWithTheSameIdAreEqual() {
        WorkshopImpl a = new WorkshopImpl(1, "Java", "basics");
        WorkshopImpl b = new WorkshopImpl(1, "Different name", "different description");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void workshopsWithDifferentIdsAreNotEqual() {
        WorkshopImpl a = new WorkshopImpl(1, "Java", "basics");
        WorkshopImpl b = new WorkshopImpl(2, "Java", "basics");

        assertNotEquals(a, b);
    }

    @Test
    void workshopIsNeverEqualToNull() {
        WorkshopImpl a = new WorkshopImpl(1, "Java", "basics");

        assertFalse(a.equals(null));
    }

    @Test
    void equalityDoesNotDependOnTheInstructor() {
        WorkshopImpl a = new WorkshopImpl(1, "Java", "basics");
        WorkshopImpl b = new WorkshopImpl(1, "Java", "basics");
        b.setInstructor(new InstructorImpl("i1", "Dana", "dana@univ.edu"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void listContainsFindsAWorkshopById() {
        List<WorkshopImpl> workshops = new ArrayList<>();
        workshops.add(new WorkshopImpl(1, "Java", "basics"));

        assertTrue(workshops.contains(new WorkshopImpl(1, "Other", "other")));
        assertFalse(workshops.contains(new WorkshopImpl(2, "Java", "basics")));
    }
}
