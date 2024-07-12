package com.example.solvro_task.service;

import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.request.TaskChangeRequest;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.dto.response.TaskAssignmentResponse;
import com.example.solvro_task.dto.response.TaskResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectService {
    void createProject(ProjectCreationRequest request);

    ProjectResponse findById(Long projectId);

    DeveloperProjectsResponse getProjectsByEmail(String email);

    void createTask(TaskCreationRequest request, Long projectId);

    TaskResponse changeTask(TaskChangeRequest request, Long projectId, Long taskId);

    List<TaskAssignmentResponse> assignTasks(Long projectId);

    ResponseEntity<?> acceptTaskAssignment(Long projectId, Long assignId, boolean accepted);
}
