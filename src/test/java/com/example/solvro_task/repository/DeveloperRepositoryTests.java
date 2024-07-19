package com.example.solvro_task.repository;

import com.example.solvro_task.entity.Developer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static com.example.solvro_task.entity.enums.Specialization.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DeveloperRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DeveloperRepository developerRepository;

    @Test
    @DisplayName("findByEmail")
    public void developerRepository_findByEmail_returnDeveloper() {
        // arrange
        Developer developer = Developer.builder()
                .email("developer@example.com")
                .specialization(BACKEND)
                .build();
        entityManager.persistAndFlush(developer);

        // act
        Optional<Developer> foundDev = developerRepository.findByEmail(developer.getEmail());

        // assert
        assertThat(foundDev.isPresent()).isTrue();
        assertThat(foundDev.get()).isEqualTo(developer);
    }

    @Test
    @DisplayName("findAllByBackendSpecialization")
    public void developerRepository_findAllByBackendSpecialization_returnAllBackendDevelopers() {
        Developer developer1 = Developer.builder()
                .email("dev1@x.com")
                .specialization(BACKEND)
                .build();
        Developer developer2 = Developer.builder()
                .email("dev2@x.com")
                .specialization(DEVOPS)
                .build();
        Developer developer3 = Developer.builder()
                .email("dev3@x.com")
                .specialization(BACKEND)
                .build();
        entityManager.persist(developer1);
        entityManager.persist(developer2);
        entityManager.persist(developer3);

        List<Developer> backendDevelopers = developerRepository.findAllBySpecialization(BACKEND);

        assertThatList(backendDevelopers).hasSize(2);
        assertThatList(backendDevelopers).containsOnly(developer3, developer1);
    }
}
