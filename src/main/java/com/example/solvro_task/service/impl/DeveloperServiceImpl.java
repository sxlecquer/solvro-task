package com.example.solvro_task.service.impl;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.repository.DeveloperRepository;
import com.example.solvro_task.service.DeveloperService;
import org.springframework.stereotype.Service;

@Service
public record DeveloperServiceImpl(DeveloperRepository developerRepository) implements DeveloperService {

    @Override
    public void save(Developer developer) {
        developerRepository.save(developer);
    }
}
