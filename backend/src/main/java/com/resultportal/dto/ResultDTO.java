package com.resultportal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResultDTO {

    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String studentName;
    private String registrationNumber;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private String subjectName;
    private String subjectCode;

    @NotNull(message = "Marks are required")
    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Double marks;

    private String grade;

    @NotNull(message = "Semester is required")
    private Integer semester;
}
