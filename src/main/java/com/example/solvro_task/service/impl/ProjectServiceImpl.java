package com.example.solvro_task.service.impl;

import com.example.solvro_task.dto.request.TaskChangeRequest;
import com.example.solvro_task.dto.response.TaskResponse;
import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
import com.example.solvro_task.entity.Task;
import com.example.solvro_task.entity.TaskCredentials;
import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.repository.ProjectRepository;
import com.example.solvro_task.service.DeveloperService;
import com.example.solvro_task.service.ProjectService;
import com.example.solvro_task.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.solvro_task.entity.enums.TaskState.*;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final DeveloperService developerService;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, DeveloperService developerService, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.developerService = developerService;
        this.taskService = taskService;
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
        }
        Task task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name(request.name())
                        .estimation(request.estimation())
                        .specialization(request.specialization())
                        .assignedDeveloper(assignedDeveloper)
                        .build())
                .state(assignedDeveloper != null ? IN_PROGRESS : TODO)
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
        task.setState(request.state());
        Developer assignedDeveloper;
        if(request.assignedDeveloper() != null) {
            assignedDeveloper = developerService.findByEmail(request.assignedDeveloper())
                    .orElseThrow(() -> new IllegalArgumentException("Developer not found by email: " + request.assignedDeveloper()));
            if(assignedDeveloper.getSpecialization() != task.getTaskCredentials().getSpecialization())
                throw new IllegalArgumentException("Task specialization mismatches assigned developer");
            task.getTaskCredentials().setAssignedDeveloper(assignedDeveloper);
        }
        taskService.save(task);
        Project project = projectRepository.findById(projectId).orElseThrow();
        TaskCredentials taskCredentials = task.getTaskCredentials();
        String assignedDevEmail = taskCredentials.getAssignedDeveloper() != null ? taskCredentials.getAssignedDeveloper().getEmail() : null;
        return new TaskResponse(project.getName(), taskCredentials.getName(), taskCredentials.getEstimation(),
                taskCredentials.getSpecialization(), assignedDevEmail, task.getState(), task.getCreatedAt());
    }
}
