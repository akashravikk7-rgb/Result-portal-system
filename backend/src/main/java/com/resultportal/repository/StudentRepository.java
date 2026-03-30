package com.resultportal.repository;

import com.resultportal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    Optional<Student> findByEmail(String email);
    List<Student> findByDepartment(String department);
    Boolean existsByRegistrationNumber(String registrationNumber);
    Boolean existsByEmail(String email);
}
