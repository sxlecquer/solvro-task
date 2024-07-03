package com.example.solvro_task.service.impl;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.Project;
import com.example.solvro_task.model.ProjectCreationRequest;
import com.example.solvro_task.repository.ProjectRepository;
import com.example.solvro_task.service.DeveloperService;
import com.example.solvro_task.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final DeveloperService developerService;

    public ProjectServiceImpl(ProjectRepository projectRepository, DeveloperService developerService) {
        this.projectRepository = projectRepository;
        this.developerService = developerService;
    }

    @Override
    public void createProject(ProjectCreationRequest request) {
        List<Developer> developers = request.developerEmails().stream()
                .map(email -> {
                    Developer developer = developerService.findByEmail(email);
                    if(developer == null) {
                        throw new IllegalArgumentException("Developer not found for email: " + email);
                    }
                    return developer;
                })
                .toList();
        if(developers.isEmpty()) {
            throw new IllegalArgumentException("There are no developers assigned to this project.");
        }

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .developers(developers)
                .build();
        projectRepository.save(project);

        for(Developer developer : developers) {
            developer.getProjects().add(project);
            developerService.save(developer);
        }
    }
}
