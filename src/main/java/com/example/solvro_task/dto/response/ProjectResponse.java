package com.example.solvro_task.dto.response;

import com.example.solvro_task.dto.DeveloperModel;

import java.util.List;

public record ProjectResponse(String name, String description, List<DeveloperModel> developers) {
}
