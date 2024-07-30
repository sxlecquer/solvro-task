package com.example.solvro_task.controller;

import com.example.solvro_task.dto.DeveloperModel;
import com.example.solvro_task.dto.request.ProjectCreationRequest;
import com.example.solvro_task.dto.request.TaskChangeRequest;
import com.example.solvro_task.dto.request.TaskCreationRequest;
import com.example.solvro_task.dto.response.DeveloperProjectsResponse;
import com.example.solvro_task.dto.response.ProjectResponse;
import com.example.solvro_task.dto.response.TaskResponse;
import com.example.solvro_task.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.example.solvro_task.entity.enums.Specialization.*;
import static com.example.solvro_task.entity.enums.TaskState.*;
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
                .andExpect(content().string(containsStringIgnoringCase("created")));

        ArgumentCaptor<ProjectCreationRequest> requestCaptor = ArgumentCaptor.forClass(ProjectCreationRequest.class);
        verify(projectService).createProject(requestCaptor.capture());
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    @Test
    @DisplayName("createProject_badRequest")
    public void projectService_createProject_returnBadRequest() throws Exception {
        ProjectCreationRequest request = new ProjectCreationRequest("", " ", Arrays.asList("incorrect", null));
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", containsStringIgnoringCase("blank")))
                .andExpect(jsonPath("$.description", containsStringIgnoringCase("blank")))
                .andExpect(jsonPath("$['developerEmails[0]']", containsStringIgnoringCase("email")))
                .andExpect(jsonPath("$['developerEmails[1]']", containsStringIgnoringCase("null")));
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

    @Test
    @DisplayName("createTask")
    public void projectService_createTask_returnSuccessMessage() throws Exception {
        Long projectId = 1L;
        TaskCreationRequest request = new TaskCreationRequest("startup", 89, DEVOPS, "dev@x.com");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/project/{id}/task", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("created")));

        ArgumentCaptor<TaskCreationRequest> requestCaptor = ArgumentCaptor.forClass(TaskCreationRequest.class);
        verify(projectService).createTask(requestCaptor.capture(), eq(projectId));
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    @Test
    @DisplayName("createTask_badRequest")
    public void projectService_createTask_returnBadRequest() throws Exception {
        Long projectId = 1L;
        TaskCreationRequest request = new TaskCreationRequest(" ", -19, null, "incorrect");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/project/{id}/task", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", containsStringIgnoringCase("blank")))
                .andExpect(jsonPath("$.estimation", containsStringIgnoringCase("fibonacci")))
                .andExpect(jsonPath("$.specialization", containsStringIgnoringCase("null")))
                .andExpect(jsonPath("$.assignedDeveloper", containsStringIgnoringCase("email")));
    }

    @Test
    @DisplayName("changeTask")
    public void projectService_changeTask_returnTaskDto() throws Exception {
        Long projectId = 1L;
        Long taskId = 2L;
        TaskChangeRequest request = new TaskChangeRequest(IN_PROGRESS, "new_dev@x.com");
        String requestJson = objectMapper.writeValueAsString(request);
        TaskResponse response = new TaskResponse("startup", "task", 5, UX_UI,
                request.assignedDeveloper(), request.state(), LocalDateTime.now());
        when(projectService.changeTask(request, projectId, taskId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/project/{id}/task/{taskId}", projectId, taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName", is(response.projectName())))
                .andExpect(jsonPath("$.name", is(response.name())))
                .andExpect(jsonPath("$.estimation", is(response.estimation())))
                .andExpect(jsonPath("$.specialization", is(response.specialization().toString())))
                .andExpect(jsonPath("$.assignedDeveloper", is(response.assignedDeveloper())))
                .andExpect(jsonPath("$.state", is(response.state().toString())))
                .andExpect(jsonPath("$.createdAt", Matchers.matchesPattern("^" + response.createdAt().toString().replaceAll("\\.\\d+", "") + ".*$")));

        verify(projectService).changeTask(request, projectId, taskId);
    }

    @Test
    @DisplayName("changeTask_badRequest")
    public void projectService_changeTask_returnBadRequest() throws Exception {
        Long projectId = 1L;
        Long taskId = 2L;
        TaskChangeRequest request = new TaskChangeRequest(null, "incorrect");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.patch("/project/{id}/task/{taskId}", projectId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.state", containsStringIgnoringCase("null")))
                .andExpect(jsonPath("$.assignedDeveloper", containsStringIgnoringCase("email")));
    }

    @Test
    @DisplayName("changeTask_badRequest_specMismatch")
    public void projectService_changeTask_returnBadRequestDueToSpecMismatch() throws Exception {
        Long projectId = 1L;
        Long taskId = 2L;
        TaskChangeRequest request = new TaskChangeRequest(DONE, "spec_mismatch@x.com");
        String requestJson = objectMapper.writeValueAsString(request);
        when(projectService.changeTask(request, projectId, taskId)).thenThrow(new IllegalArgumentException("Task specialization mismatches assigned developer"));

        mockMvc.perform(MockMvcRequestBuilders.patch("/project/{id}/task/{taskId}", projectId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("specialization mismatch")));
    }
}
