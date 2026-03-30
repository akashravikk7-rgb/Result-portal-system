package com.resultportal.service;

import com.resultportal.dto.SubjectDTO;
import com.resultportal.exception.BadRequestException;
import com.resultportal.exception.ResourceNotFoundException;
import com.resultportal.model.Subject;
import com.resultportal.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SubjectDTO getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
        return toDTO(subject);
    }

    public List<SubjectDTO> getSubjectsBySemester(Integer semester) {
        return subjectRepository.findBySemester(semester).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SubjectDTO> getSubjectsByDepartment(String department) {
        return subjectRepository.findByDepartment(department).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SubjectDTO createSubject(SubjectDTO dto) {
        if (subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new BadRequestException("Subject code already exists: "
                    + dto.getSubjectCode());
        }

        Subject subject = toEntity(dto);
        Subject saved = subjectRepository.save(subject);
        return toDTO(saved);
    }

    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));

        subject.setSubjectName(dto.getSubjectName());
        subject.setSubjectCode(dto.getSubjectCode());
        subject.setSemester(dto.getSemester());
        subject.setDepartment(dto.getDepartment());

        Subject updated = subjectRepository.save(subject);
        return toDTO(updated);
    }

    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
        subjectRepository.delete(subject);
    }

    public long getSubjectCount() {
        return subjectRepository.count();
    }

    // ─── Mapping helpers ──────────────────────────────────────────────

    private SubjectDTO toDTO(Subject subject) {
        return SubjectDTO.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .subjectCode(subject.getSubjectCode())
                .semester(subject.getSemester())
                .department(subject.getDepartment())
                .build();
    }

    private Subject toEntity(SubjectDTO dto) {
        return Subject.builder()
                .subjectName(dto.getSubjectName())
                .subjectCode(dto.getSubjectCode())
                .semester(dto.getSemester())
                .department(dto.getDepartment())
                .build();
    }
}
