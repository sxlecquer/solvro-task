package com.example.solvro_task.model;

import java.util.List;

public record ProjectCreationRequest(String name, String description, List<String> developerEmails) {
}
