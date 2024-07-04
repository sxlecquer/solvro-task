package com.example.solvro_task.controller;

import com.example.solvro_task.model.response.DeveloperProjectsResponse;
import com.example.solvro_task.model.request.ProjectCreationRequest;
import com.example.solvro_task.model.response.ProjectResponse;
import com.example.solvro_task.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    public void createProject(@RequestBody ProjectCreationRequest request) {
        log.info("new project creation request: {}", request);
        projectService.createProject(request);
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
}
