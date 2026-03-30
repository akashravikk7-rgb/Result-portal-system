package com.resultportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SubjectDTO {

    private Long id;

    @NotBlank(message = "Subject name is required")
    private String subjectName;

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    @NotNull(message = "Semester is required")
    private Integer semester;

    @NotBlank(message = "Department is required")
    private String department;
}
