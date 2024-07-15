package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
import com.example.solvro_task.entity.Task;
import com.example.solvro_task.entity.TaskCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Test
    public void taskRepository_findByIdAndProjectId_returnTask() {
        // arrange
        Project project = Project.builder()
                .name("project")
                .description("description")
                .build();
        Task task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(13)
                        .specialization(UX_UI)
                        .build())
                .state(IN_PROGRESS)
                .project(project)
                .build();
        Project savedProject = entityManager.persistAndFlush(project);
        Task savedTask = entityManager.persistAndFlush(task);

        // act
        Optional<Task> foundTask = taskRepository.findByIdAndProjectId(savedTask.getId(), savedProject.getId());

        // assert
        assertThat(foundTask.isPresent()).isTrue();
        assertThat(foundTask.get().getTaskCredentials().getName()).isEqualTo(task.getTaskCredentials().getName());
    }

    @Test
    public void taskRepository_findByProjectIdAndDeveloper_returnMoreThanOneTask() {
        Project project = Project.builder()
                .name("project")
                .description("description")
                .build();
        Developer developer = Developer.builder()
                .email("dev@x.com")
                .specialization(FRONTEND)
                .projects(Set.of(project))
                .build();
        Long projectId = (Long) entityManager.persistAndGetId(project);
        entityManager.persist(developer);

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

        List<Task> taskList = taskRepository.findByProjectIdAndDeveloper(projectId, developer);

        assertThat(taskList).hasSize(2);
        assertThat(taskList).containsExactlyInAnyOrder(task1, task2);
    }

    @Test
    public void taskRepository_findUnassignedTasksByProjectId_returnMoreThanOneTask() {
        Project project = Project.builder()
                .name("project")
                .description("description")
                .build();
        Developer developer = Developer.builder()
                .email("dev@x.com")
                .specialization(FRONTEND)
                .build();
        Long projectId = (Long) entityManager.persistAndGetId(project);
        entityManager.persist(developer);

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

        List<Task> taskList = taskRepository.findUnassignedTasksByProjectId(projectId);

        assertThat(taskList).hasSize(2);
        assertThat(taskList).containsOnly(task1, task3);
    }
}
