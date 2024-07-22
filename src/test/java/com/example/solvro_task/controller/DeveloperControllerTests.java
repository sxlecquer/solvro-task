package com.example.solvro_task.controller;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.service.DeveloperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = DeveloperController.class)
@AutoConfigureMockMvc
public class DeveloperControllerTests {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeveloperService developerService;

    @Test
    public void developerController_registerDeveloper_returnSuccessMessage() throws Exception {
        DeveloperModel developerModel = new DeveloperModel("dev@x.com", BACKEND);
        String developerJson = objectMapper.writeValueAsString(developerModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/developer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(developerJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Developer registration successful"));

        ArgumentCaptor<DeveloperModel> captor = ArgumentCaptor.forClass(DeveloperModel.class);
        verify(developerService).registerDeveloper(captor.capture());
        assertThat(captor.getValue()).isEqualTo(developerModel);
    }
}
