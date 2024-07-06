package com.example.solvro_task.service;

import com.example.solvro_task.model.request.ProjectCreationRequest;
import com.example.solvro_task.model.request.TaskCreationRequest;
import com.example.solvro_task.model.response.DeveloperProjectsResponse;
import com.example.solvro_task.model.response.ProjectResponse;

public interface ProjectService {
    void createProject(ProjectCreationRequest request);

    ProjectResponse findById(Long projectId);

    DeveloperProjectsResponse getProjectsByEmail(String email);

    void createTask(TaskCreationRequest request, Long projectId);
}
