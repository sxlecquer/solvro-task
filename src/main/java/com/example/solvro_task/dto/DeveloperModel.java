package com.example.solvro_task.dto;

import com.example.solvro_task.entity.enums.Specialization;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DeveloperModel(
        @NotNull(message = "Email address cannot be null")
        @Email(message = "Incorrect email address provided") String email,
        @NotNull(message = "Developer specialization cannot be null") Specialization specialization) {
}
