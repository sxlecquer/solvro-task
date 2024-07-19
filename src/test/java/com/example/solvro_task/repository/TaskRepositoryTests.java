package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
import com.example.solvro_task.entity.Task;
import com.example.solvro_task.entity.TaskCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static com.example.solvro_task.entity.enums.TaskState.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Project project;
    private Developer developer;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .name("project")
                .description("description")
                .build();
        developer = Developer.builder()
                .email("dev@x.com")
                .specialization(FRONTEND)
                .build();
        entityManager.persistAndFlush(project);
        entityManager.persistAndFlush(developer);
    }

    @Test
    @DisplayName("findByIdAndProjectId")
    public void taskRepository_findByIdAndProjectId_returnTask() {
        // arrange
        Task task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(13)
                        .specialization(FRONTEND)
                        .assignedDeveloper(developer)
                        .build())
                .state(IN_PROGRESS)
                .project(project)
                .build();
        Task savedTask = entityManager.persistAndFlush(task);

        // act
        Optional<Task> foundTask = taskRepository.findByIdAndProjectId(savedTask.getId(), project.getId());

        // assert
        assertThat(foundTask.isPresent()).isTrue();
        assertThat(foundTask.get().getTaskCredentials().getName()).isEqualTo(task.getTaskCredentials().getName());
    }

    @Test
    @DisplayName("findByProjectIdAndDeveloper")
    public void taskRepository_findByProjectIdAndDeveloper_returnMoreThanOneTask() {
        Task task1 = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task1")
                        .assignedDeveloper(developer)
                        .build())
                .state(TODO)
                .project(project)
                .build();
        Task task2 = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task2")
                        .assignedDeveloper(developer)
                        .build())
                .state(TODO)
                .project(project)
                .build();
        entityManager.persist(task1);
        entityManager.persist(task2);

        List<Task> taskList = taskRepository.findByProjectIdAndDeveloper(project.getId(), developer);

        assertThat(taskList).hasSize(2);
        assertThat(taskList).containsExactlyInAnyOrder(task1, task2);
    }

    @Test
    @DisplayName("findUnassignedTasksByProjectId")
    public void taskRepository_findUnassignedTasksByProjectId_returnMoreThanOneTask() {
        Task task1 = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task1")
                        .build())
                .state(TODO)
                .project(project)
                .build();
        Task task2 = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task2")
                        .assignedDeveloper(developer)
                        .build())
                .state(TODO)
                .project(project)
                .build();
        Task task3 = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task3")
                        .build())
                .state(DONE)
                .project(project)
                .build();
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);

        List<Task> taskList = taskRepository.findUnassignedTasksByProjectId(project.getId());

        assertThat(taskList).hasSize(2);
        assertThat(taskList).containsOnly(task1, task3);
    }
}
