package com.example.solvro_task.service;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.dto.DeveloperModel;

import java.util.Optional;

public interface DeveloperService {
    void registerDeveloper(DeveloperModel dev);

    void save(Developer developer);

    Optional<Developer> findByEmail(String email);
}
