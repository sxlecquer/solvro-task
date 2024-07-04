package com.example.solvro_task.service;

import com.example.solvro_task.model.ProjectCreationRequest;
import com.example.solvro_task.model.ProjectResponse;

public interface ProjectService {
    void createProject(ProjectCreationRequest request);

    ProjectResponse findById(Long projectId);
}
