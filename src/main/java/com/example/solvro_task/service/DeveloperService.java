package com.example.solvro_task.service;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.model.DeveloperModel;

public interface DeveloperService {
    void registerDeveloper(DeveloperModel dev);

    void save(Developer developer);

    Developer findByEmail(String email);
}
