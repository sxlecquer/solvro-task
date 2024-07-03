package com.example.solvro_task.model;

import com.example.solvro_task.entity.Developer;

import java.util.List;

public record ProjectCreationRequest(String projectName, List<Developer> developers) {
}
