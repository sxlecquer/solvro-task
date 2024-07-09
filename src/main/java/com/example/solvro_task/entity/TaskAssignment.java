package com.example.solvro_task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_assign")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "dev_email",
            referencedColumnName = "email"
    )
    private Developer developer;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "task_id",
            referencedColumnName = "id"
    )
    private Task task;
}
