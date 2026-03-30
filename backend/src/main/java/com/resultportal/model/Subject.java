package com.resultportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Subject name is required")
    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @NotBlank(message = "Subject code is required")
    @Column(name = "subject_code", nullable = false, unique = true)
    private String subjectCode;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private String department;
}
