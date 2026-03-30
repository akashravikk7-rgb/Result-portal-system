package com.resultportal.service;

import com.resultportal.dto.StudentDTO;
import com.resultportal.exception.BadRequestException;
import com.resultportal.exception.ResourceNotFoundException;
import com.resultportal.model.Student;
import com.resultportal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return toDTO(student);
    }

    public StudentDTO getStudentByRegistrationNumber(String regNumber) {
        Student student = studentRepository.findByRegistrationNumber(regNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "registrationNumber", regNumber));
        return toDTO(student);
    }

    public List<StudentDTO> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO createStudent(StudentDTO dto) {
        if (studentRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new BadRequestException("Registration number already exists: "
                    + dto.getRegistrationNumber());
        }
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists: " + dto.getEmail());
        }

        Student student = toEntity(dto);
        Student saved = studentRepository.save(student);
        return toDTO(saved);
    }

    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setDepartment(dto.getDepartment());
        student.setRegistrationNumber(dto.getRegistrationNumber());
        student.setSemester(dto.getSemester());

        Student updated = studentRepository.save(student);
        return toDTO(updated);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        studentRepository.delete(student);
    }

    public long getStudentCount() {
        return studentRepository.count();
    }

    // ─── Mapping helpers ──────────────────────────────────────────────

    public StudentDTO toDTO(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .department(student.getDepartment())
                .registrationNumber(student.getRegistrationNumber())
                .semester(student.getSemester())
                .build();
    }

    private Student toEntity(StudentDTO dto) {
        return Student.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .registrationNumber(dto.getRegistrationNumber())
                .semester(dto.getSemester())
                .build();
    }
}
