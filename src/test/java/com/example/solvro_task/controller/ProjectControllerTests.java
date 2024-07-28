package com.example.solvro_task.controller;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProjectController.class)
@AutoConfigureMockMvc
public class ProjectControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @Test
    @DisplayName("createProject")
    public void projectService_createProject_returnSuccessMessage() throws Exception {
        ProjectCreationRequest request = new ProjectCreationRequest("startup", "description", List.of("dev1@x.com", "dev2@x.com"));
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Project created successfully"));

        ArgumentCaptor<ProjectCreationRequest> requestCaptor = ArgumentCaptor.forClass(ProjectCreationRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    @Test
    @DisplayName("getProject")
    public void projectService_getProject_returnProjectDto() throws Exception {
        Long projectId = 1L;
        ProjectResponse response = new ProjectResponse("startup", "description",
                List.of(new DeveloperModel("dev1@x.com", BACKEND),
                        new DeveloperModel("dev2@x.com", FRONTEND))
        );
        when(projectService.findById(projectId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/project/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(response.name())))
                .andExpect(jsonPath("$.description", is(response.description())))
                .andExpect(jsonPath("$.developers.size()", is(response.developers().size())));
        verify(projectService).findById(projectId);
    }

    @Test
    @DisplayName("getProjectsByEmail")
    public void projectService_getProjectsByEmail_returnDeveloperProjectsDto() throws Exception {
        String email = "dev1@x.com";
        DeveloperProjectsResponse response = new DeveloperProjectsResponse(List.of(
                new ProjectResponse("startup1", "description", List.of(
                        new DeveloperModel("dev1@x.com", BACKEND),
                        new DeveloperModel("dev2@x.com", FRONTEND)
                )),
                new ProjectResponse("startup2", "description", List.of(
                        new DeveloperModel("dev1@x.com", BACKEND),
                        new DeveloperModel("dev3@x.com", UX_UI)
                ))
        ));
        when(projectService.getProjectsByEmail(email)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/project")
                .queryParam("email", email)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects.size()", is(response.projects().size())))
                .andExpect(jsonPath("$.projects[0].name", is(response.projects().get(0).name())))
                .andExpect(jsonPath("$.projects[1].name", is(response.projects().get(1).name())))
                .andExpect(jsonPath("$.projects[0].description", is(response.projects().get(0).description())))
                .andExpect(jsonPath("$.projects[1].description", is(response.projects().get(1).description())))
                .andExpect(jsonPath("$.projects[0].developers.size()", is(response.projects().get(0).developers().size())))
                .andExpect(jsonPath("$.projects[1].developers.size()", is(response.projects().get(1).developers().size())));

        verify(projectService).getProjectsByEmail(email);
    }
}
