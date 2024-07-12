package com.example.solvro_task.controller;

import com.example.solvro_task.dto.request.TaskChangeRequest;
import com.example.solvro_task.dto.response.TaskAssignmentResponse;
import com.example.solvro_task.dto.response.TaskResponse;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> createProject(@Valid @RequestBody ProjectCreationRequest request) {
        log.info("new project creation request: {}", request);
        projectService.createProject(request);
        return ResponseEntity.ok("Project created successfully");
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable("id") Long projectId) {
        log.info("get project with id: {}", projectId);
        return projectService.findById(projectId);
    }

    @GetMapping
    public DeveloperProjectsResponse getProjectsByEmail(@RequestParam("email") String email) {
        log.info("get developer projects by email: {}", email);
        return projectService.getProjectsByEmail(email);
    }

    @PostMapping("/{id}/task")
    @Transactional
    public ResponseEntity<String> createTask(@Valid @RequestBody TaskCreationRequest request, @PathVariable("id") Long projectId) {
        log.info("create task for project with id: {}", projectId);
        projectService.createTask(request, projectId);
        return ResponseEntity.ok("Task created successfully");
    }

    @PatchMapping("/{id}/task/{taskId}")
    @Transactional
    public TaskResponse changeTask(@Valid @RequestBody TaskChangeRequest request, @PathVariable("id") Long projectId, @PathVariable Long taskId) {
        log.info("change task with id: {} for project id: {}", taskId, projectId);
        return projectService.changeTask(request, projectId, taskId);
    }

    @PostMapping("/{id}/task/assignment")
    @Transactional
    public List<TaskAssignmentResponse> assignTasks(@PathVariable("id") Long projectId) {
        log.info("assign tasks for project with id: {}", projectId);
        return projectService.assignTasks(projectId);
    }

    @PatchMapping("/{id}/task/assignment/{assignId}")
    @Transactional
    public ResponseEntity<?> acceptTaskAssignment(@PathVariable("id") Long projectId, @PathVariable Long assignId, @RequestParam boolean accepted) {
        log.info("accept task assignment with id: {} for project id: {} ", assignId, projectId);
        return projectService.acceptTaskAssignment(projectId, assignId, accepted);
    }
}
