package com.example.solvro_task.entity.enums;

import com.example.solvro_task.entity.enums.processing.TaskStateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TaskStateDeserializer.class)
public enum TaskState {
    TODO,
    IN_PROGRESS,
    DONE
}
