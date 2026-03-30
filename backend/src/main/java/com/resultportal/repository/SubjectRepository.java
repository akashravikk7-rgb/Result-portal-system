package com.resultportal.repository;

import com.resultportal.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);
    List<Subject> findBySemester(Integer semester);
    List<Subject> findByDepartment(String department);
    List<Subject> findBySemesterAndDepartment(Integer semester, String department);
    Boolean existsBySubjectCode(String subjectCode);
}
