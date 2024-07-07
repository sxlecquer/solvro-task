package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.id = :id and t.project.id = :projectId")
    Optional<Task> findByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);
}
