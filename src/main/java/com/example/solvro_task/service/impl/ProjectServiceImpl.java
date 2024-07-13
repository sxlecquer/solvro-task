package com.example.solvro_task.service.impl;

import com.example.solvro_task.dto.request.TaskChangeRequest;
import com.example.solvro_task.dto.response.TaskAssignmentResponse;
import com.example.solvro_task.dto.response.TaskResponse;
import com.example.solvro_task.entity.*;
import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.entity.enums.Specialization;
import com.example.solvro_task.repository.ProjectRepository;
import com.example.solvro_task.repository.TaskAssignmentRepository;
import com.example.solvro_task.service.DeveloperService;
import com.example.solvro_task.service.ProjectService;
import com.example.solvro_task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.solvro_task.entity.enums.TaskState.*;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final DeveloperService developerService;
    private final TaskService taskService;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, DeveloperService developerService, TaskService taskService, TaskAssignmentRepository taskAssignmentRepository) {
        this.projectRepository = projectRepository;
        this.developerService = developerService;
        this.taskService = taskService;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    @Override
    public void createProject(ProjectCreationRequest request) {
        List<Developer> developers = request.developerEmails().stream()
                .map(email -> developerService.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Developer not found by email: " + email)))
                .toList();

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .developers(developers)
                .build();
        projectRepository.save(project);

        for(Developer developer : developers) {
            developer.getProjects().add(project);
            developerService.save(developer);
        }
    }

    @Override
    public ProjectResponse findById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found by id: " + projectId));
        List<DeveloperModel> developers = project.getDevelopers().stream()
                .map(dev -> new DeveloperModel(dev.getEmail(), dev.getSpecialization()))
                .toList();
        return new ProjectResponse(project.getName(), project.getDescription(), developers);
    }

    @Override
    public DeveloperProjectsResponse getProjectsByEmail(String email) {
        Developer developer = developerService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Developer not found by email: " + email));

        List<ProjectResponse> projects = new ArrayList<>();
        for(Project project : developer.getProjects()) {
            List<DeveloperModel> developers = project.getDevelopers().stream()
                    .map(dev -> new DeveloperModel(dev.getEmail(), dev.getSpecialization()))
                    .toList();
            projects.add(new ProjectResponse(project.getName(), project.getDescription(), developers));
        }
        return new DeveloperProjectsResponse(projects);
    }

    @Override
    public void createTask(TaskCreationRequest request, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found by id: " + projectId));
        Developer assignedDeveloper = null;
        if(request.assignedDeveloper() != null) {
            assignedDeveloper = developerService.findByEmail(request.assignedDeveloper())
                    .orElseThrow(() -> new IllegalArgumentException("Developer not found by email: " + request.assignedDeveloper()));
            if(assignedDeveloper.getSpecialization() != request.specialization())
                throw new IllegalArgumentException("Task specialization mismatches assigned developer");
            assignedDeveloper.getProjects().add(project);
            developerService.save(assignedDeveloper);
        }
        Task task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name(request.name())
                        .estimation(request.estimation())
                        .specialization(request.specialization())
                        .assignedDeveloper(assignedDeveloper)
                        .build())
                .state(TODO)
                .project(project)
                .build();
        taskService.save(task);

        project.getTasks().add(task);
        projectRepository.save(project);
    }

    @Override
    public TaskResponse changeTask(TaskChangeRequest request, Long projectId, Long taskId) {
        Task task = taskService.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found by id: " + taskId + " for project id: " + projectId));
        Project project = projectRepository.findById(projectId).orElseThrow();
        task.setState(request.state());
        Developer assignedDeveloper;
        if(request.assignedDeveloper() != null) {
            assignedDeveloper = developerService.findByEmail(request.assignedDeveloper())
                    .orElseThrow(() -> new IllegalArgumentException("Developer not found by email: " + request.assignedDeveloper()));
            if(assignedDeveloper.getSpecialization() != task.getTaskCredentials().getSpecialization())
                throw new IllegalArgumentException("Task specialization mismatches assigned developer");
            Developer previous = task.getTaskCredentials().getAssignedDeveloper();
            if(taskService.findByProjectIdAndDeveloper(projectId, previous).size() == 1) {
                previous.getProjects().remove(project);
                developerService.save(previous);
            }
            task.getTaskCredentials().setAssignedDeveloper(assignedDeveloper);
            assignedDeveloper.getProjects().add(project);
            developerService.save(assignedDeveloper);
        }
        taskService.save(task);
        TaskCredentials taskCredentials = task.getTaskCredentials();
        String assignedDevEmail = taskCredentials.getAssignedDeveloper() != null ? taskCredentials.getAssignedDeveloper().getEmail() : null;
        return new TaskResponse(project.getName(), taskCredentials.getName(), taskCredentials.getEstimation(),
                taskCredentials.getSpecialization(), assignedDevEmail, task.getState(), task.getCreatedAt());
    }

    @Override
    public List<TaskAssignmentResponse> assignTasks(Long projectId) {
        List<TaskAssignmentResponse> result = new ArrayList<>();
        String projectName = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found by id: " + projectId))
                .getName();
        List<Task> unassignedTasks = taskService.findUnassignedTasksByProjectId(projectId);
        Map<Specialization, List<Developer>> specializationMap = new HashMap<>();
        Arrays.stream(Specialization.values()).forEach(spec ->
                specializationMap.put(spec, developerService.findAllBySpecialization(spec)));
        unassignedTasks.forEach(taskAssignmentRepository::deleteByTask);

        for(Task unassignedTask : unassignedTasks) {
            TaskCredentials taskCredentials = unassignedTask.getTaskCredentials();
            List<Developer> developers = specializationMap.get(taskCredentials.getSpecialization());
            List<Developer> freeDevelopers = developers.stream()
                    .filter(dev -> dev.getProjects().stream()
                            .allMatch(project -> project.getTasks().stream()
                                    .filter(t -> Objects.equals(t.getTaskCredentials().getAssignedDeveloper(), dev))
                                    .allMatch(t -> t.getState() == DONE)))
                    .toList();
            Developer assignedDeveloper = getDevWithMinEstimation(!freeDevelopers.isEmpty() ? freeDevelopers : developers);
            if(assignedDeveloper == null)
                throw new IllegalStateException("No developers found by specialization: " + taskCredentials.getSpecialization());
            unassignedTask.getTaskCredentials().setAssignedDeveloper(assignedDeveloper); // rollback these changes
            taskAssignmentRepository.save(TaskAssignment.builder()
                    .developer(assignedDeveloper)
                    .task(unassignedTask)
                    .build()
            );

            TaskAssignmentResponse response = TaskAssignmentResponse.builder()
                    .projectName(projectName)
                    .taskName(taskCredentials.getName())
                    .specialization(taskCredentials.getSpecialization())
                    .devEmail(assignedDeveloper.getEmail())
                    .build();
            result.add(response);
        }
        unassignedTasks.forEach(t -> t.getTaskCredentials().setAssignedDeveloper(null));
        return result;
    }

    @Override
    public ResponseEntity<?> acceptTaskAssignment(Long projectId, Long assignId, boolean accepted) {
        TaskAssignment taskAssignment = taskAssignmentRepository.findByIdAndProjectId(assignId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("Task assignment not found by id: " + assignId + " for project id: " + projectId));
        taskAssignmentRepository.delete(taskAssignment);
        if(accepted) {
            Task task = taskAssignment.getTask();
            Developer developer = taskAssignment.getDeveloper();
            TaskCredentials taskCredentials = task.getTaskCredentials();

            taskCredentials.setAssignedDeveloper(developer);
            taskService.save(task);

            developer.getProjects().add(task.getProject());
            developerService.save(developer);

            return new ResponseEntity<>(new TaskResponse(task.getProject().getName(), taskCredentials.getName(),
                    taskCredentials.getEstimation(), taskCredentials.getSpecialization(), developer.getEmail(),
                    task.getState(), task.getCreatedAt()), HttpStatus.ACCEPTED);
        }
        return ResponseEntity.ok("Task assignment rejected");
    }

    private Developer getDevWithMinEstimation(List<Developer> developers) {
        Developer result = null;
        double minEstimation = Float.MAX_VALUE;
        for(Developer developer : developers) {
            double averageEstimation = developer.getProjects().stream()
                    .flatMap(p -> p.getTasks().stream())
                    .filter(t -> Objects.equals(t.getTaskCredentials().getAssignedDeveloper(), developer))
                    .mapToInt(t -> t.getTaskCredentials().getEstimation())
                    .average()
                    .orElse(0);
            if(averageEstimation < minEstimation) {
                minEstimation = averageEstimation;
                result = developer;
            }
        }
        return result;
    }
}
