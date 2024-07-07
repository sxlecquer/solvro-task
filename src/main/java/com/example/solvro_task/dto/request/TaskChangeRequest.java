package com.example.solvro_task.dto.request;

import com.example.solvro_task.entity.enums.TaskState;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record TaskChangeRequest(
        @NotNull(message = "Task state cannot be null") TaskState state,
        @Email(message = "Incorrect email address provided") String assignedDeveloper) {
}
