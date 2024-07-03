package com.example.solvro_task.entity.enums.converter;

import com.example.solvro_task.entity.enums.Specialization;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;

public class SpecializationDeserializer extends JsonDeserializer<Specialization> {

    @Override
    public Specialization deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        String value = parser.getText();
        try {
            return Specialization.valueOf(value.toUpperCase().replace("/", "_"));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Incorrect specialization: '" + value
                    + "'. Available specializations: " + Arrays.stream(Specialization.values()).map(Specialization::getTitle).toList());
        }
    }
}
