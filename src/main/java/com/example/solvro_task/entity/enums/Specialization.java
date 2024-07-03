package com.example.solvro_task.entity.enums;

import com.example.solvro_task.entity.enums.converter.SpecializationDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonDeserialize(using = SpecializationDeserializer.class)
public enum Specialization {
    FRONTEND("FRONTEND"),
    BACKEND("BACKEND"),
    DEVOPS("DEVOPS"),
    UX_UI("UX/UI");

    private final String title;
}
