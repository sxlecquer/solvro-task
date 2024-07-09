package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    Optional<Developer> findByEmail(String email);

    List<Developer> findAllBySpecialization(Specialization specialization);
}
