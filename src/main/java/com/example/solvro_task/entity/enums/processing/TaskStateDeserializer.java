package com.example.solvro_task.entity.enums.processing;

import com.example.solvro_task.entity.enums.TaskState;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class TaskStateDeserializer extends JsonDeserializer<TaskState> {

    @Override
    public TaskState deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        String value = parser.getText();
        try {
            return TaskState.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch(IllegalArgumentException ex) {
            throw new IllegalArgumentException("Incorrect task state: '" + value
                    + "'. Available states: " + Arrays.stream(TaskState.values()).map(state -> state.name().replace("_", " ")).toList());
        }
    }
}
