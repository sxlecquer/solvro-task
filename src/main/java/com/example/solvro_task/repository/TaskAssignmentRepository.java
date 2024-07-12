package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Task;
import com.example.solvro_task.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    @Query("select a from TaskAssignment a where a.id = :id and a.task.project.id = :projectId")
    Optional<TaskAssignment> findByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);

    void deleteByTask(Task task);
}
