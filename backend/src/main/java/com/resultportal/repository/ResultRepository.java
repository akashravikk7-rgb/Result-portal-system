package com.resultportal.repository;

import com.resultportal.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByStudentId(Long studentId);
    List<Result> findByStudentIdAndSemester(Long studentId, Integer semester);
    List<Result> findBySubjectId(Long subjectId);
    Boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);
}
