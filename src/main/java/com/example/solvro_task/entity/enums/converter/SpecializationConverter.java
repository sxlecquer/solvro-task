package com.example.solvro_task.entity.enums.converter;

import com.example.solvro_task.entity.enums.Specialization;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SpecializationConverter implements AttributeConverter<Specialization, String> {

    @Override
    public String convertToDatabaseColumn(Specialization attribute) {
        if(attribute == null) {
            return null;
        }
        return attribute.getTitle();
    }

    @Override
    public Specialization convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        return Specialization.valueOf(dbData.toUpperCase().replace("/", "_"));
    }
}
