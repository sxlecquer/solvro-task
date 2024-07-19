package com.example.solvro_task.service;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.entity.enums.Specialization;
import com.example.solvro_task.repository.DeveloperRepository;
import com.example.solvro_task.service.impl.DeveloperServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeveloperServiceImplTests {
    @Mock
    private DeveloperRepository developerRepository;

    @InjectMocks
    private DeveloperServiceImpl developerService;

    private Developer developer;

    @BeforeEach
    public void setUp() {
        developer = Developer.builder()
                .email("dev@x.com")
                .specialization(BACKEND)
                .build();
    }

    @Test
    public void developerService_registerDeveloper_thenSaveDeveloper() {
        // arrange
        DeveloperModel devModel = new DeveloperModel(developer.getEmail(), developer.getSpecialization());

        when(developerRepository.save(any(Developer.class))).thenReturn(developer);

        // act
        developerService.registerDeveloper(devModel);

        // assert
        ArgumentCaptor<Developer> developerCaptor = ArgumentCaptor.forClass(Developer.class);
        verify(developerRepository, times(1)).save(developerCaptor.capture());
        assertThat(developerCaptor.getValue()).isEqualTo(developer);
    }

    @Test
    public void developerService_save_thenSaveDeveloper() {
        when(developerRepository.save(any(Developer.class))).thenReturn(developer);

        developerService.save(developer);

        ArgumentCaptor<Developer> developerCaptor = ArgumentCaptor.forClass(Developer.class);
        verify(developerRepository).save(developerCaptor.capture());
        assertThat(developerCaptor.getValue()).isEqualTo(developer);
    }

    @Test
    public void developerService_findByEmail_returnDeveloper() {
        when(developerRepository.findByEmail(developer.getEmail())).thenReturn(Optional.of(developer));
        
        Optional<Developer> foundDeveloper = developerService.findByEmail(developer.getEmail());
        
        assertThat(foundDeveloper.isPresent()).isTrue();
        assertThat(foundDeveloper.get()).isEqualTo(developer);
    }

    @Test
    public void developerService_findAllBySpecialization_returnMoreThanOneDeveloper() {
        Specialization specialization = BACKEND;
        Developer developer1 = Developer.builder()
                .email("dev1@x.com").specialization(specialization).build();
        List<Developer> developerList = Arrays.asList(developer1, developer);
        when(developerRepository.findAllBySpecialization(specialization)).thenReturn(developerList);

        List<Developer> foundDevelopers = developerService.findAllBySpecialization(specialization);

        assertThatList(foundDevelopers).hasSize(2);
        assertThatList(foundDevelopers).containsOnly(developer, developer1);
    }
}
