package patmal.workshops.student.impl.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.logic.WorkshopSystemImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

        // The demo students come from a CSV file: the one given as a program
        // argument, or the students.csv bundled in the application resources.
        File studentsCsv = args.length > 0 ? new File(args[0]) : bundledStudentsCsv();
        system.importStudentDataFromFile(studentsCsv);
        log.info("Demo students imported from {}", studentsCsv);

        system.createTeam("Alpha", java, List.of("s1", "s2", "s3"));
        // s4 intentionally stays a free student

        File folder = new File(".").getAbsoluteFile();
        system.exportInstructorsDataToJson(new File("instructors.json"));
        system.exportStudentsDataToJson(new File("students.json"));
        system.exportTeamsDataToJson(new File("teams.json"));

        log.info("Demo data created: 1 instructor, 2 workshops, 4 students, 1 team (s4 is free)");
        log.info("JSON exports written to {}", folder.getParent());
    }

    /**
     * Copies the students.csv resource to a temporary file, so that it can be
     * read as a File even when the application runs from inside the jar.
     */
    private static File bundledStudentsCsv() {
        try (InputStream in = Main.class.getResourceAsStream("/students.csv")) {
            Path temp = Files.createTempFile("students", ".csv");
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            temp.toFile().deleteOnExit();
            return temp.toFile();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read the bundled students.csv", e);
        }
    }
}
