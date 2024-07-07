package com.example.solvro_task.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProjectCreationRequest(
        @NotBlank(message = "Project name cannot be blank") String name,
        @NotBlank(message = "Project description cannot be blank") String description,
        @NotEmpty(message = "Developers must be assigned to the project")
        List<@NotNull(message = "Email address cannot be null")
                @Email(message = "Incorrect email address provided") String> developerEmails) {
}
