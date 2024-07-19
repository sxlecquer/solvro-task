package com.example.solvro_task.repository;

import com.example.solvro_task.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private Project project;
    private Task task;
    private TaskAssignment taskAssignment;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .name("project")
                .description("description")
                .tasks(new ArrayList<>())
                .build();
        Developer developer = Developer.builder()
                .email("dev@x.com")
                .specialization(FRONTEND)
                .build();
        entityManager.persistAndFlush(project);
        entityManager.persistAndFlush(developer);

        task = Task.builder()
                .createdAt(LocalDateTime.now())
                .taskCredentials(TaskCredentials.builder()
                        .name("task")
                        .estimation(21)
                        .specialization(FRONTEND)
                        .build())
                .state(IN_PROGRESS)
                .project(project)
                .build();
        project.getTasks().add(task);
        entityManager.persist(task);

        taskAssignment = TaskAssignment.builder()
                .task(task)
                .developer(developer)
                .build();
        entityManager.persistAndFlush(taskAssignment);
    }

    @Test
    @DisplayName("findByIdAndProjectId")
    public void taskAssignmentRepository_findByIdAndProjectId_returnTaskAssignment() {
        Optional<TaskAssignment> foundTaskAssignment = taskAssignmentRepository.findByIdAndProjectId(taskAssignment.getId(), project.getId());

        assertThat(foundTaskAssignment.isPresent()).isTrue();
        assertThat(foundTaskAssignment.get().getTask()).isEqualTo(task);
    }

    @Test
    @DisplayName("deleteByTask")
    public void taskAssignmentRepository_deleteByTask_returnEmptyTaskAssignment() {
        taskAssignmentRepository.deleteByTask(task);
        entityManager.detach(taskAssignment);
        Optional<TaskAssignment> foundTaskAssignment = taskAssignmentRepository.findById(taskAssignment.getId());

        assertThat(foundTaskAssignment).isEmpty();
    }
}
