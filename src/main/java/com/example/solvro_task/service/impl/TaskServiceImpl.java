package com.example.solvro_task.service.impl;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Task;
import com.example.solvro_task.repository.TaskRepository;
import com.example.solvro_task.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record TaskServiceImpl(TaskRepository taskRepository) implements TaskService {

    @Override
    public void save(Task task) {
        taskRepository.save(task);
    }

    @Override
    public Optional<Task> findByIdAndProjectId(Long taskId, Long projectId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId);
    }

    @Override
    public List<Task> findUnassignedTasksByProjectId(Long projectId) {
        return taskRepository.findUnassignedTasksByProjectId(projectId);
    }

    @Override
    public List<Task> findByProjectIdAndDeveloper(Long projectId, Developer developer) {
        return developer != null ? taskRepository.findByProjectIdAndDeveloper(projectId, developer) : List.of();
    }
}
