package com.example.solvro_task.entity;

import com.example.solvro_task.entity.enums.TaskState;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;

    @Embedded
    private TaskCredentials taskCredentials;

    @Enumerated(EnumType.STRING)
    private TaskState taskState;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id"
    )
    private Project project;
}
