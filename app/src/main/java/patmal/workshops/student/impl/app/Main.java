package patmal.workshops.student.impl.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.logic.WorkshopSystemImpl;

import java.io.File;
import java.util.List;

/**
 * workshop-cli: fills a WorkshopSystem with demo data and exports it to
 * instructors.json, students.json and teams.json in the current folder.
 */
public final class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        WorkshopSystem system = new WorkshopSystemImpl();

        Instructor dana = system.createInstructor("i1", "Dana Levi", "dana.levi@univ.edu");
        Workshop java = system.createWorkshop("Java Workshop", "From objects to streams");
        Workshop spring = system.createWorkshop("Spring Workshop", "Dependency injection in practice");
        system.associateWorkshopWithInstructor(dana, java);
        system.associateWorkshopWithInstructor(dana, spring);

        system.createStudent("s1", "Alice Cohen", "alice@students.edu");
        system.createStudent("s2", "Bob Friedman", "bob@students.edu");
        system.createStudent("s3", "Charlie Mizrahi", "charlie@students.edu");
        system.createStudent("s4", "David Peretz", "david@students.edu");

        system.createTeam("Alpha", java, List.of("s1", "s2", "s3"));
        // s4 intentionally stays a free student

        File folder = new File(".").getAbsoluteFile();
        system.exportInstructorsDataToJson(new File("instructors.json"));
        system.exportStudentsDataToJson(new File("students.json"));
        system.exportTeamsDataToJson(new File("teams.json"));

        log.info("Demo data created: 1 instructor, 2 workshops, 4 students, 1 team (s4 is free)");
        log.info("JSON exports written to {}", folder.getParent());
    }
}
