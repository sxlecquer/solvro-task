package com.example.solvro_task.service;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    void save(Task task);

    Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);

    List<Task> findUnassignedTasksByProjectId(Long projectId);

    List<Task> findByProjectIdAndDeveloper(Long projectId, Developer developer);
}
