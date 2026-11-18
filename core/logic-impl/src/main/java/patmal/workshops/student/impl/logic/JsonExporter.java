package patmal.workshops.student.impl.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Person;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.model.StudentImpl;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Builds the three JSON export documents of API 2.0. Every document is a tree:
 * no object points back to one of its ancestors. A missing relation is written
 * as the JSON value null. Exports never change the system.
 */
class JsonExporter {

    private static final Logger log = LogManager.getLogger(JsonExporter.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final InstructorManager instructorManager;
    private final StudentManager studentManager;
    private final TeamManager teamManager;

    JsonExporter(InstructorManager instructorManager,
                 StudentManager studentManager,
                 TeamManager teamManager) {
        this.instructorManager = instructorManager;
        this.studentManager = studentManager;
        this.teamManager = teamManager;
    }

    /**
     * Instructor -> workshops -> teams -> student details.
     */
    void exportInstructors(File target) {
        ArrayNode root = mapper.createArrayNode();
        for (Instructor instructor : instructorManager.getAll()) {
            ObjectNode instructorNode = person(instructor);
            ArrayNode workshopsNode = instructorNode.putArray("workshops");
            for (Workshop workshop : instructor.getWorkshops()) {
                ObjectNode workshopNode = workshopCore(workshop);
                ArrayNode teamsNode = workshopNode.putArray("teams");
                for (Team team : teamManager.getAll()) {
                    if (workshop.equals(team.getWorkshop())) {
                        ObjectNode teamNode = teamsNode.addObject();
                        teamNode.put("id", team.getId());
                        teamNode.put("name", team.getName());
                        ArrayNode studentsNode = teamNode.putArray("students");
                        for (Student student : team.getStudents()) {
                            studentsNode.add(person(student));
                        }
                    }
                }
                workshopsNode.add(workshopNode);
            }
            root.add(instructorNode);
        }
        write(target, root, "instructors");
    }

    /**
     * Student -> team (with the ids of its students) -> workshop -> instructor.
     */
    void exportStudents(File target) {
        ArrayNode root = mapper.createArrayNode();
        for (Student student : studentManager.getAll()) {
            ObjectNode studentNode = person(student);
            Team team = ((StudentImpl) student).getTeam();
            if (team == null) {
                studentNode.putNull("team");
            } else {
                ObjectNode teamNode = studentNode.putObject("team");
                teamNode.put("id", team.getId());
                teamNode.put("name", team.getName());
                ArrayNode idsNode = teamNode.putArray("studentIds");
                for (Student member : team.getStudents()) {
                    idsNode.add(member.getId());
                }
                putWorkshopWithInstructor(teamNode, team.getWorkshop());
            }
            root.add(studentNode);
        }
        write(target, root, "students");
    }

    /**
     * Team -> student details, workshop -> instructor.
     */
    void exportTeams(File target) {
        ArrayNode root = mapper.createArrayNode();
        for (Team team : teamManager.getAll()) {
            ObjectNode teamNode = root.addObject();
            teamNode.put("id", team.getId());
            teamNode.put("name", team.getName());
            ArrayNode studentsNode = teamNode.putArray("students");
            for (Student student : team.getStudents()) {
                studentsNode.add(person(student));
            }
            putWorkshopWithInstructor(teamNode, team.getWorkshop());
        }
        write(target, root, "teams");
    }

    private ObjectNode person(Person person) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", person.getId());
        node.put("name", person.getName());
        node.put("email", person.getEmail());
        return node;
    }

    private ObjectNode workshopCore(Workshop workshop) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", workshop.getId());
        node.put("name", workshop.getName());
        node.put("description", workshop.getDescription());
        return node;
    }

    private void putWorkshopWithInstructor(ObjectNode parent, Workshop workshop) {
        if (workshop == null) {
            parent.putNull("workshop");
            return;
        }
        ObjectNode workshopNode = workshopCore(workshop);
        Instructor instructor = workshop.getInstructor();
        if (instructor == null) {
            workshopNode.putNull("instructor");
        } else {
            workshopNode.set("instructor", person(instructor));
        }
        parent.set("workshop", workshopNode);
    }

    private void write(File target, ArrayNode root, String what) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(target, root);
        } catch (IOException e) {
            log.warn("Failed to write {} export to '{}'", what, target);
            throw new UncheckedIOException("Cannot write JSON export to " + target, e);
        }
        log.info("Exported {} to '{}'", what, target);
    }
}
