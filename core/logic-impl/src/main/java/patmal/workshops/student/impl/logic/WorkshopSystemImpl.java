package patmal.workshops.student.impl.logic;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.io.File;
import java.util.List;

/**
 * The single entry point of the system. It is intentionally thin: every call
 * is delegated to the manager that owns the area (instructors, students,
 * workshops or teams).
 */
public class WorkshopSystemImpl implements WorkshopSystem {

    private final InstructorManager instructorManager = new InstructorManager();
    private final StudentManager studentManager = new StudentManager();
    private final WorkshopManager workshopManager = new WorkshopManager();
    private final TeamManager teamManager = new TeamManager(studentManager);
    private final JsonExporter jsonExporter = new JsonExporter(instructorManager, studentManager, teamManager);

    public WorkshopSystemImpl() {
    }

    @Override
    public Instructor createInstructor(String id, String name, String email) {
        return instructorManager.create(id, name, email);
    }

    @Override
    public Student createStudent(String id, String name, String email) {
        return studentManager.create(id, name, email);
    }

    @Override
    public Workshop createWorkshop(String name, String description) {
        return workshopManager.create(name, description);
    }

    @Override
    public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        workshopManager.associateWithInstructor(instructor, workshop);
    }

    @Override
    public Team createTeam(String name, Workshop workshop, List<String> members) {
        return teamManager.create(name, workshop, members);
    }

    @Override
    public List<Instructor> getInstructors() {
        return instructorManager.getAll();
    }

    @Override
    public Instructor getInstructor(String id) {
        return instructorManager.byId(id);
    }

    @Override
    public List<Student> getStudents() {
        return studentManager.getAll();
    }

    @Override
    public Student getStudent(String id) {
        return studentManager.byId(id);
    }

    @Override
    public List<Workshop> getWorkshops() {
        return workshopManager.getAll();
    }

    @Override
    public Workshop getWorkshop(Integer id) {
        return workshopManager.byId(id);
    }

    @Override
    public List<Team> getTeams() {
        return teamManager.getAll();
    }

    @Override
    public Team getTeam(Integer id) {
        return teamManager.byId(id);
    }

    @Override
    public List<Student> getFreeStudents() {
        return studentManager.getFree();
    }

    @Override
    public List<Student> getStudentsOfInstructor(String instructorId) {
        return teamManager.getStudentsOfInstructor(instructorManager.byId(instructorId));
    }

    @Override
    public List<Student> getStudentsOfWorkshop(Integer workshopId) {
        return teamManager.getStudentsOfWorkshop(workshopManager.byId(workshopId));
    }

    @Override
    public void exportInstructorsDataToJson(File targetFileName) {
        jsonExporter.exportInstructors(targetFileName);
    }

    @Override
    public void exportStudentsDataToJson(File targetFileName) {
        jsonExporter.exportStudents(targetFileName);
    }

    @Override
    public void exportTeamsDataToJson(File targetFileName) {
        jsonExporter.exportTeams(targetFileName);
    }
}
