package com.example.solvro_task.entity;

import com.example.solvro_task.entity.enums.Specialization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private Specialization specialization;

    @ManyToMany(cascade = {PERSIST, REFRESH})
    @JoinTable(
            joinColumns = @JoinColumn(
                    name = "developer_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "project_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Project> projects = new HashSet<>();
}
