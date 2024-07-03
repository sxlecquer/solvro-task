package com.example.solvro_task.service.impl;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.model.DeveloperModel;
import com.example.solvro_task.repository.DeveloperRepository;
import com.example.solvro_task.service.DeveloperService;
import org.springframework.stereotype.Service;

@Service
public record DeveloperServiceImpl(DeveloperRepository developerRepository) implements DeveloperService {

    @Override
    public void registerDeveloper(DeveloperModel dev) {
        if(!dev.email().matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^@_!#$%&’*+/=?`{|}~^.-][A-Za-z0-9_-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Incorrect email address provided.");
        }

        Developer developer = Developer.builder()
                .email(dev.email())
                .specialization(dev.specialization())
                .build();
        developerRepository.save(developer);
    }

    @Override
    public void save(Developer developer) {
        developerRepository.save(developer);
    }

    @Override
    public Developer findByEmail(String email) {
        return developerRepository.findByEmail(email);
    }
}
