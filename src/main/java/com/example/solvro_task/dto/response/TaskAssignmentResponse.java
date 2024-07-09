package com.example.solvro_task.dto.response;

import com.example.solvro_task.entity.enums.Specialization;
import lombok.Builder;

@Builder
public record TaskAssignmentResponse(String projectName, String taskName,
                                     Specialization specialization, String devEmail) {
}
