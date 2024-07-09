package com.example.solvro_task.service.impl;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.entity.enums.Specialization;
import com.example.solvro_task.repository.DeveloperRepository;
import com.example.solvro_task.service.DeveloperService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record DeveloperServiceImpl(DeveloperRepository developerRepository) implements DeveloperService {

    @Override
    public void registerDeveloper(DeveloperModel dev) {
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
    public Optional<Developer> findByEmail(String email) {
        return developerRepository.findByEmail(email);
    }

    @Override
    public List<Developer> findAllBySpecialization(Specialization specialization) {
        return developerRepository.findAllBySpecialization(specialization);
    }
}
