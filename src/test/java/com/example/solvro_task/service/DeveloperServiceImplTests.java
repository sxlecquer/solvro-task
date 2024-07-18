package com.example.solvro_task.service;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.entity.Developer;
import com.example.solvro_task.repository.DeveloperRepository;
import com.example.solvro_task.service.impl.DeveloperServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeveloperServiceImplTests {
    @Mock
    private DeveloperRepository developerRepository;

    @InjectMocks
    private DeveloperServiceImpl developerService;

    @Test
    public void developerService_registerDeveloper_thenSaveDeveloper() {
        // arrange
        DeveloperModel devModel = new DeveloperModel("dev@x.com", BACKEND);
        Developer developer = Developer.builder()
                        .email(devModel.email())
                        .specialization(devModel.specialization())
                        .build();

        when(developerRepository.save(any(Developer.class))).thenReturn(developer);

        // act
        developerService.registerDeveloper(devModel);

        // assert
        ArgumentCaptor<Developer> developerCaptor = ArgumentCaptor.forClass(Developer.class);
        verify(developerRepository, times(1)).save(developerCaptor.capture());
        assertThat(developerCaptor.getValue()).isEqualTo(developer);
    }
}
