package com.example.solvro_task.service;

import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    public void setUp() {
        developer = Developer.builder()
                .email("dev@x.com")
                .specialization(BACKEND)
                .projects(new HashSet<>())
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
}
