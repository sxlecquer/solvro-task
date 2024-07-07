package com.example.solvro_task.dto.response;

import com.example.solvro_task.entity.enums.Specialization;
import com.example.solvro_task.entity.enums.TaskState;

import java.time.LocalDateTime;

public record TaskResponse(String projectName, String name, int estimation, Specialization specialization,
                           String assignedDeveloper, TaskState state, LocalDateTime createdAt) {
}
