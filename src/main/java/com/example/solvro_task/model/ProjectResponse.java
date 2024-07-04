package com.example.solvro_task.model;

import java.util.List;

public record ProjectResponse(String name, String description, List<DeveloperModel> developers) {
}
