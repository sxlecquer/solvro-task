package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.id = :id and t.project.id = :projectId")
    Optional<Task> findByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);

    @Query("select t from Task t where t.project.id = :projectId and t.taskCredentials.assignedDeveloper = :dev")
    List<Task> findByProjectIdAndDeveloper(@Param("projectId") Long projectId, @Param("dev") Developer developer);

    @Query("select t from Task t where t.project.id = :projectId and t.taskCredentials.assignedDeveloper is null")
    List<Task> findUnassignedTasksByProjectId(@Param("projectId") Long projectId);
}
