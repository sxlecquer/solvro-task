package com.example.solvro_task.controller;

import com.example.solvro_task.model.ProjectCreationRequest;
import com.example.solvro_task.model.ProjectResponse;
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
        return projectService.findById(projectId);
    }
}
