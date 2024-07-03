package com.example.solvro_task.controller;

import com.example.solvro_task.model.DeveloperModel;
import com.example.solvro_task.service.DeveloperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/developer")
@Slf4j
public class DeveloperController {
    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping
    public void registerDeveloper(@RequestBody DeveloperModel developer) {
        log.info("new developer registration request: {}", developer);
        developerService.registerDeveloper(developer);
    }
}
