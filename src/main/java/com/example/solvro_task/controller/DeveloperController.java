package com.example.solvro_task.controller;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.service.DeveloperService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> registerDeveloper(@Valid @RequestBody DeveloperModel developer) {
        log.info("new developer registration request: {}", developer);
        developerService.registerDeveloper(developer);
        return ResponseEntity.ok("Developer registration successful");
    }
}
