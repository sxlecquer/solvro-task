package com.example.solvro_task.entity;

import com.example.solvro_task.entity.enums.Specialization;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCredentials {
    private String name;
    private int estimation;
    private Specialization specialization;

    @ManyToOne
    @JoinColumn(
            name = "assigned_dev_id",
            referencedColumnName = "id"
    )
    private Developer assignedDeveloper;
}
