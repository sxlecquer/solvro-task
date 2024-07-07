package com.example.solvro_task.service;

import com.example.solvro_task.entity.Task;

import java.util.Optional;

public interface TaskService {
    void save(Task task);

    Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);
}
