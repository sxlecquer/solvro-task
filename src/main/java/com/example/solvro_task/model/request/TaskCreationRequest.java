package com.example.solvro_task.model.request;

import com.example.solvro_task.annotation.FibonacciNumber;
import com.example.solvro_task.entity.enums.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskCreationRequest(
        @NotBlank(message = "Task name cannot be blank") String name,
        @FibonacciNumber(message = "Task estimation must be a Fibonacci number") int estimation,
        @NotNull(message = "Task specialization cannot be null") Specialization specialization,
        Long assignedDeveloper) {
}
