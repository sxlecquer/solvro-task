package com.example.solvro_task.service;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
import com.example.solvro_task.entity.Task;
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

import java.util.*;

import static com.example.solvro_task.entity.enums.Specialization.*;
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
    @DisplayName("createTaskWITHOUTAssignedDeveloper")
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
    @DisplayName("createTaskWITHAssignedDeveloper")
    public void projectService_createTaskWithAssignedDeveloper_thenSaveTask() {
        Long projectId = 1L;
        Developer dev = Developer.builder().email("dev1@x.com").specialization(FRONTEND).projects(new HashSet<>()).build();
        TaskCreationRequest request = new TaskCreationRequest("task", 144, FRONTEND, dev.getEmail());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(dev));

        projectService.createTask(request, projectId);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getTaskCredentials().getName()).isEqualTo(request.name());
        assertThat(savedTask.getTaskCredentials().getEstimation()).isEqualTo(request.estimation());
        assertThat(savedTask.getTaskCredentials().getSpecialization()).isEqualTo(request.specialization());
        assertThat(savedTask.getTaskCredentials().getAssignedDeveloper()).isEqualTo(dev);
        assertThat(savedTask.getTaskCredentials().getAssignedDeveloper().getProjects()).contains(project);
    }

    @Test
    @DisplayName("createTaskWithWrongAssignedDeveloperSpec")
    public void projectService_createTaskWithAssignedDeveloper_throwExceptionOfSpecMismatch() {
        Long projectId = 1L;
        TaskCreationRequest request = new TaskCreationRequest("task", 144, FRONTEND, developer.getEmail());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(developerService.findByEmail(anyString())).thenReturn(Optional.of(developer));

        assertThatIllegalArgumentException().isThrownBy(() -> projectService.createTask(request, projectId)).withMessageContaining("specialization mismatch");
    }
}
