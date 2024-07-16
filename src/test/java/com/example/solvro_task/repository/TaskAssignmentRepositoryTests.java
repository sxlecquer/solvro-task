package com.example.solvro_task.repository;

import com.example.solvro_task.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static com.example.solvro_task.entity.enums.TaskState.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TaskAssignmentRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Test
    public void taskAssignmentRepository_findByIdAndProjectId_returnTaskAssignment() {
        // arrange
        Project project = Project.builder()
                .name("project")
                .description("description")
                .tasks(new ArrayList<>())
                .build();
        Task task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(21)
                        .specialization(DEVOPS)
                        .build())
                .state(IN_PROGRESS)
                .project(project)
                .build();
        project.getTasks().add(task);
        Long projectId = entityManager.persistAndGetId(project, Long.class);

        Developer developer = Developer.builder()
                .email("dev@x.com")
                .specialization(DEVOPS)
                .build();
        TaskAssignment taskAssignment = TaskAssignment.builder()
                .task(task)
                .developer(developer)
                .build();
        entityManager.persist(developer);
        TaskAssignment savedTaskAssignment = entityManager.persistAndFlush(taskAssignment);

        // act
        Optional<TaskAssignment> foundTaskAssignment = taskAssignmentRepository.findByIdAndProjectId(savedTaskAssignment.getId(), projectId);

        // assert
        assertThat(foundTaskAssignment.isPresent()).isTrue();
        assertThat(foundTaskAssignment.get().getTask()).isEqualTo(task);
    }

    @Test
    public void taskAssignmentRepository_deleteByTask_returnEmptyTaskAssignment() {
        Project project = Project.builder()
                .name("project")
                .build();
        Task task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(3)
                        .specialization(FRONTEND)
                        .build())
                .state(DONE)
                .project(project)
                .build();
        entityManager.persist(project);
        entityManager.persist(task);

        Developer developer = Developer.builder()
                .email("dev@x.com")
                .specialization(FRONTEND)
                .build();
        TaskAssignment taskAssignment = TaskAssignment.builder()
                .task(task)
                .developer(developer)
                .build();
        entityManager.persist(developer);

        taskAssignmentRepository.saveAndFlush(taskAssignment);
        taskAssignmentRepository.deleteByTask(task);
        entityManager.detach(taskAssignment);
        Optional<TaskAssignment> foundTaskAssignment = taskAssignmentRepository.findById(taskAssignment.getId());

        assertThat(foundTaskAssignment).isEmpty();
    }
}
