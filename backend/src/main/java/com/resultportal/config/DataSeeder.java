package com.resultportal.config;

import com.resultportal.model.Student;
import com.resultportal.model.Subject;
import com.resultportal.model.User;
import com.resultportal.model.Result;
import com.resultportal.repository.StudentRepository;
import com.resultportal.repository.SubjectRepository;
import com.resultportal.repository.UserRepository;
import com.resultportal.repository.ResultRepository;
import com.resultportal.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private ResultRepository resultRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ResultService resultService;

    @Override
    public void run(String... args) {
        // Only seed if database is empty
        if (userRepository.count() > 0) return;

        System.out.println("🌱 Seeding database with sample data...");

        // Create admin user
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(admin);

        // Create sample students
        Student s1 = studentRepository.save(Student.builder()
                .name("Rahul Sharma")
                .email("rahul@example.com")
                .department("Computer Science")
                .registrationNumber("CS2024001")
                .semester(4)
                .build());

        Student s2 = studentRepository.save(Student.builder()
                .name("Priya Patel")
                .email("priya@example.com")
                .department("Computer Science")
                .registrationNumber("CS2024002")
                .semester(4)
                .build());

        Student s3 = studentRepository.save(Student.builder()
                .name("Amit Kumar")
                .email("amit@example.com")
                .department("Electronics")
                .registrationNumber("EC2024001")
                .semester(3)
                .build());

        // Create student user accounts
        userRepository.save(User.builder()
                .username("rahul")
                .password(passwordEncoder.encode("student123"))
                .role(User.Role.STUDENT)
                .student(s1)
                .build());

        userRepository.save(User.builder()
                .username("priya")
                .password(passwordEncoder.encode("student123"))
                .role(User.Role.STUDENT)
                .student(s2)
                .build());

        userRepository.save(User.builder()
                .username("amit")
                .password(passwordEncoder.encode("student123"))
                .role(User.Role.STUDENT)
                .student(s3)
                .build());

        // Create subjects
        Subject sub1 = subjectRepository.save(Subject.builder()
                .subjectName("Data Structures")
                .subjectCode("CS301")
                .semester(3).department("Computer Science").build());

        Subject sub2 = subjectRepository.save(Subject.builder()
                .subjectName("Operating Systems")
                .subjectCode("CS302")
                .semester(3).department("Computer Science").build());

        Subject sub3 = subjectRepository.save(Subject.builder()
                .subjectName("Database Management")
                .subjectCode("CS401")
                .semester(4).department("Computer Science").build());

        Subject sub4 = subjectRepository.save(Subject.builder()
                .subjectName("Computer Networks")
                .subjectCode("CS402")
                .semester(4).department("Computer Science").build());

        Subject sub5 = subjectRepository.save(Subject.builder()
                .subjectName("Software Engineering")
                .subjectCode("CS403")
                .semester(4).department("Computer Science").build());

        Subject sub6 = subjectRepository.save(Subject.builder()
                .subjectName("Digital Electronics")
                .subjectCode("EC301")
                .semester(3).department("Electronics").build());

        Subject sub7 = subjectRepository.save(Subject.builder()
                .subjectName("Signal Processing")
                .subjectCode("EC302")
                .semester(3).department("Electronics").build());

        // Create sample results for Rahul (Semester 3 & 4)
        saveResult(s1, sub1, 85.0, 3);
        saveResult(s1, sub2, 78.0, 3);
        saveResult(s1, sub3, 92.0, 4);
        saveResult(s1, sub4, 88.0, 4);
        saveResult(s1, sub5, 75.0, 4);

        // Results for Priya
        saveResult(s2, sub1, 95.0, 3);
        saveResult(s2, sub2, 89.0, 3);
        saveResult(s2, sub3, 91.0, 4);
        saveResult(s2, sub4, 84.0, 4);
        saveResult(s2, sub5, 93.0, 4);

        // Results for Amit
        saveResult(s3, sub6, 72.0, 3);
        saveResult(s3, sub7, 68.0, 3);

        System.out.println("✅ Database seeded successfully!");
        System.out.println("   Admin login: admin / admin123");
        System.out.println("   Student login: rahul / student123");
    }

    private void saveResult(Student student, Subject subject, Double marks, Integer semester) {
        resultRepository.save(Result.builder()
                .student(student)
                .subject(subject)
                .marks(marks)
                .grade(resultService.calculateGrade(marks))
                .semester(semester)
                .build());
    }
}
