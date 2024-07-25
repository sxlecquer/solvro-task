package com.example.solvro_task.service;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.request.TaskChangeRequest;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.dto.response.TaskAssignmentResponse;
import com.example.solvro_task.dto.response.TaskResponse;
import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
import com.example.solvro_task.entity.Task;
import com.example.solvro_task.entity.TaskCredentials;
import com.example.solvro_task.entity.enums.Specialization;
import com.example.solvro_task.repository.ProjectRepository;
import com.example.solvro_task.repository.TaskAssignmentRepository;
import com.example.solvro_task.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static com.example.solvro_task.entity.enums.TaskState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTests {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DeveloperService developerService;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskAssignmentRepository taskAssignmentRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Developer developer;
    private Project project;
    private Task task;

    @BeforeEach
    public void setUp() {
        developer = Developer.builder()
                .email("dev@x.com")
                .specialization(BACKEND)
                .projects(new HashSet<>())
                .build();
        project = Project.builder()
                .name("startup")
                .description("description")
                .developers(List.of(developer))
                .tasks(new ArrayList<>())
                .build();
        task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(144)
                        .specialization(FRONTEND)
                        .build())
                .state(TODO)
                .project(project)
                .build();
    }

    @Test
    @DisplayName("createProject")
    public void projectService_createProject_thenSaveProject() {
        ProjectCreationRequest request = new ProjectCreationRequest("startup", "description", List.of(developer.getEmail()));
        when(developerService.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));

        projectService.createProject(request);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        Project savedProject = projectCaptor.getValue();
        assertThat(savedProject.getName()).isEqualTo(request.name());
        assertThat(savedProject.getDescription()).isEqualTo(request.description());
        assertThat(savedProject.getDevelopers()).containsExactlyInAnyOrder(developer);

        verify(developerService).save(developer);
    }

    @Test
    @DisplayName("findById")
    public void projectService_findById_returnProjectResponse() {
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.findById(projectId);

        assertThat(response.name()).isEqualTo(project.getName());
        assertThat(response.description()).isEqualTo(project.getDescription());
        assertThat(response.developers()).extracting(DeveloperModel::email, DeveloperModel::specialization)
                .containsOnly(tuple(developer.getEmail(), developer.getSpecialization()));
    }

    @Test
    @DisplayName("getProjectsByEmail")
    public void projectService_getProjectsByEmail_returnDeveloperProjectsResponse() {
        Developer dev = Developer.builder()
                .email("dev@x.com")
                .specialization(BACKEND)
                .projects(Set.of(project))
                .build();
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(dev));

        DeveloperProjectsResponse response = projectService.getProjectsByEmail(dev.getEmail());

        assertThat(response.projects()).hasSize(1);
        assertThat(response.projects()).extracting(ProjectResponse::name, ProjectResponse::description)
                .containsOnly(tuple(project.getName(), project.getDescription()));
    }

    @Test
    @DisplayName("createTask_withoutAssignedDeveloper")
    public void projectService_createTaskWithoutAssignedDeveloper_thenSaveTask() {
        Long projectId = 1L;
        TaskCreationRequest request = new TaskCreationRequest("task", 144, FRONTEND, null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.createTask(request, projectId);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getTaskCredentials().getName()).isEqualTo(request.name());
        assertThat(savedTask.getTaskCredentials().getEstimation()).isEqualTo(request.estimation());
        assertThat(savedTask.getTaskCredentials().getSpecialization()).isEqualTo(request.specialization());
        assertThat(savedTask.getTaskCredentials().getAssignedDeveloper()).isNull();
    }

    @Test
    @DisplayName("createTask")
    public void projectService_createTaskWithAssignedDeveloper_thenSaveTask() {
        Long projectId = 1L;
        developer.setSpecialization(FRONTEND);
        TaskCreationRequest request = new TaskCreationRequest("task", 144, FRONTEND, developer.getEmail());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(developer));

        projectService.createTask(request, projectId);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getTaskCredentials().getName()).isEqualTo(request.name());
        assertThat(savedTask.getTaskCredentials().getEstimation()).isEqualTo(request.estimation());
        assertThat(savedTask.getTaskCredentials().getSpecialization()).isEqualTo(request.specialization());
        assertThat(savedTask.getTaskCredentials().getAssignedDeveloper()).isEqualTo(developer);
        assertThat(savedTask.getTaskCredentials().getAssignedDeveloper().getProjects()).contains(project);
    }

    @Test
    @DisplayName("createTask_specMismatch")
    public void projectService_createTaskWithAssignedDeveloper_throwExceptionOfSpecMismatch() {
        Long projectId = 1L;
        TaskCreationRequest request = new TaskCreationRequest("task", 144, FRONTEND, developer.getEmail());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(developer));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> projectService.createTask(request, projectId))
                .withMessageContaining("specialization mismatch");
    }

    @Test
    @DisplayName("changeTask_withoutAssignedDeveloper")
    public void projectService_changeTaskWithoutAssignedDeveloper_returnTaskResponse() {
        Long projectId = 1L;
        Long taskId = 2L;
        TaskChangeRequest request = new TaskChangeRequest(DONE, null);
        when(taskService.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        TaskResponse response = projectService.changeTask(request, projectId, taskId);

        assertThat(response.projectName()).isEqualTo(task.getProject().getName());
        assertThat(response.name()).isEqualTo(task.getTaskCredentials().getName());
        assertThat(response.estimation()).isEqualTo(task.getTaskCredentials().getEstimation());
        assertThat(response.specialization()).isEqualTo(task.getTaskCredentials().getSpecialization());
        assertThat(response.state()).isEqualTo(DONE);
    }

    @Test
    @DisplayName("changeTask")
    public void projectService_changeTaskWithAssignedDeveloper_returnTaskResponse() {
        Long projectId = 1L;
        Long taskId = 2L;
        developer.setSpecialization(FRONTEND);
        TaskChangeRequest request = new TaskChangeRequest(DONE, developer.getEmail());
        when(taskService.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(developer));
        when(taskService.findByProjectIdAndDeveloper(eq(projectId), isNull())).thenReturn(Collections.emptyList());

        TaskResponse response = projectService.changeTask(request, projectId, taskId);

        assertThat(response.projectName()).isEqualTo(task.getProject().getName());
        assertThat(response.name()).isEqualTo(task.getTaskCredentials().getName());
        assertThat(response.estimation()).isEqualTo(task.getTaskCredentials().getEstimation());
        assertThat(response.specialization()).isEqualTo(task.getTaskCredentials().getSpecialization());
        assertThat(response.assignedDeveloper()).isEqualTo(developer.getEmail());
        assertThat(response.state()).isEqualTo(DONE);
        assertThat(developer.getProjects()).contains(project);
    }

    @Test
    @DisplayName("changeTask_specMismatch")
    public void projectService_changeTaskWithAssignedDeveloper_throwExceptionOfSpecMismatch() {
        Long projectId = 1L;
        Long taskId = 2L;
        TaskChangeRequest request = new TaskChangeRequest(DONE, developer.getEmail());
        when(taskService.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(developer));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> projectService.changeTask(request, projectId, taskId))
                .withMessageContaining("specialization mismatch");
    }

    @Test
    @DisplayName("assignTasks")
    public void projectService_assignTasks_returnTaskAssignments() {
        Long projectId = 1L;
        task.getTaskCredentials().setSpecialization(BACKEND);
        Task newTask = Task.builder()
                .taskCredentials(TaskCredentials.builder()
                        .name("new task")
                        .estimation(21)
                        .specialization(BACKEND)
                        .build())
                .state(IN_PROGRESS)
                .project(project)
                .build();
        Developer newDeveloper = Developer.builder()
                .email("new_dev@x.com")
                .specialization(BACKEND)
                .projects(new HashSet<>())
                .build();
        project.getTasks().addAll(List.of(task, newTask));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskService.findUnassignedTasksByProjectId(projectId)).thenReturn(List.of(task, newTask));
        when(developerService.findAllBySpecialization(any(Specialization.class))).then(invocation ->
                invocation.getArgument(0).equals(BACKEND) ? List.of(developer, newDeveloper) : Collections.emptyList());
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<TaskAssignmentResponse> taskAssignments = projectService.assignTasks(projectId);

        assertThat(taskAssignments).extracting(TaskAssignmentResponse::devEmail)
                .containsOnly("dev@x.com", "new_dev@x.com");
    }

    @Test
    @DisplayName("assignTasks_noDevelopersFound")
    public void projectService_assignTasks_throwExceptionNoDevelopersFound() {
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskService.findUnassignedTasksByProjectId(projectId)).thenReturn(List.of(task));
        when(developerService.findAllBySpecialization(any(Specialization.class))).thenReturn(Collections.emptyList());

        assertThatIllegalStateException()
                .isThrownBy(() -> projectService.assignTasks(projectId))
                .withMessage("No developers found by specialization: %s", task.getTaskCredentials().getSpecialization());
    }
}
