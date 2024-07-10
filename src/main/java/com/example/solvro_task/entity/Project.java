package com.example.solvro_task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_seq"
    )
    @SequenceGenerator(
            name = "project_seq",
            sequenceName = "PROJECT_SEQ",
            allocationSize = 1
    )
    private Long id;

    private String name;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(
            cascade = {PERSIST, REFRESH},
            mappedBy = "projects"
    )
    private List<Developer> developers = new ArrayList<>();

    @OneToMany(
            cascade = ALL,
            mappedBy = "project"
    )
    private List<Task> tasks = new ArrayList<>();
}
