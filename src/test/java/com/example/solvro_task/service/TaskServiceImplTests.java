package com.example.solvro_task.service;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Task;
import com.example.solvro_task.entity.TaskCredentials;
import com.example.solvro_task.repository.TaskRepository;
import com.example.solvro_task.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static com.example.solvro_task.entity.enums.TaskState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTests {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;

    @BeforeEach
    public void setUp() {
        task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(89)
                        .specialization(UX_UI)
                        .build())
                .state(TODO)
                .build();
    }

    @Test
    @DisplayName("save")
    public void taskService_save_thenSaveTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.save(task);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue()).isEqualTo(task);
    }

    @Test
    @DisplayName("findByIdAndProjectId")
    public void taskService_findByIdAndProjectId_returnTask() {
        Long taskId = 1L;
        Long projectId = 2L;
        when(taskRepository.findByIdAndProjectId(taskId, projectId)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.findByIdAndProjectId(taskId, projectId);
        assertThat(foundTask.isPresent()).isTrue();
        assertThat(foundTask.get()).isEqualTo(task);
    }

    @Test
    @DisplayName("findUnassignedTasksByProjectId")
    public void taskService_findUnassignedTasksByProjectId_returnMoreThanOneTask() {
        Long projectId = 1L;
        Task task1 = Task.builder()
                        .createdAt(LocalDateTime.now())
                        .taskCredentials(TaskCredentials.builder()
                                .name("task1")
                                .specialization(DEVOPS)
                                .estimation(144)
                                .build())
                        .state(IN_PROGRESS)
                        .build();
        List<Task> taskList = List.of(task, task1);
        when(taskRepository.findUnassignedTasksByProjectId(projectId)).thenReturn(taskList);

        List<Task> foundTasks = taskService.findUnassignedTasksByProjectId(projectId);

        assertThatList(foundTasks).hasSize(2);
        assertThatList(foundTasks).containsExactlyInAnyOrderElementsOf(taskList);
    }

    @Test
    @DisplayName("findByProjectIdAndDeveloperNotNull")
    public void taskService_findByProjectIdAndDeveloper_returnTasksWhenDeveloperIsNotNull() {
        Long projectId = 1L;
        Developer developer = Developer.builder()
                .email("dev@x.com")
                .specialization(FRONTEND)
                .build();
        Task task1 = Task.builder()
                .taskCredentials(TaskCredentials.builder().assignedDeveloper(developer).build())
                .build();
        Task task2 = Task.builder()
                .taskCredentials(TaskCredentials.builder().assignedDeveloper(developer).build())
                .build();
        List<Task> taskList = List.of(task1, task2);
        when(taskRepository.findByProjectIdAndDeveloper(projectId, developer)).thenReturn(taskList);

        List<Task> foundTasks = taskService.findByProjectIdAndDeveloper(projectId, developer);

        assertThatList(foundTasks).hasSize(2);
        assertThatList(foundTasks).containsExactlyInAnyOrderElementsOf(taskList);
    }

    @Test
    @DisplayName("findByProjectIdAndNullDeveloper")
    public void taskService_findByProjectIdAndDeveloper_returnEmptyListWhenDeveloperIsNull() {
        List<Task> foundTasks = taskService.findByProjectIdAndDeveloper(1L, null);
        assertThatList(foundTasks).isEmpty();
    }
}
