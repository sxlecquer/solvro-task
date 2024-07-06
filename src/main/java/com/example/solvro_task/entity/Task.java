package com.example.solvro_task.entity;

import com.example.solvro_task.entity.enums.TaskState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;

    @Embedded
    private TaskCredentials taskCredentials;

    @Enumerated(EnumType.STRING)
    private TaskState state;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id"
    )
    private Project project;
}
