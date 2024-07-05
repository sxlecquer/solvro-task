package com.example.solvro_task.entity;

import com.example.solvro_task.entity.enums.Specialization;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Embeddable
@Data
public class TaskCredentials {
    private String name;
    private int estimation;
    private Specialization specialization;

    @OneToOne
    @JoinColumn(
            name = "assigned_dev_id",
            referencedColumnName = "id"
    )
    private Developer assignedDeveloper;
}
