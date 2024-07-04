package com.example.solvro_task.model.response;

import com.example.solvro_task.model.DeveloperModel;

import java.util.List;

public record ProjectResponse(String name, String description, List<DeveloperModel> developers) {
}
