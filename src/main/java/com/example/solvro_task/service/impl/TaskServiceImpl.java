package com.example.solvro_task.service.impl;

import com.example.solvro_task.entity.Task;
import com.example.solvro_task.repository.TaskRepository;
import com.example.solvro_task.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public record TaskServiceImpl(TaskRepository taskRepository) implements TaskService {

    @Override
    public void save(Task task) {
        taskRepository.save(task);
    }
}
