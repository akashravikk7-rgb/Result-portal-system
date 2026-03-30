package com.resultportal.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.resultportal.dto.ResultDTO;
import com.resultportal.exception.BadRequestException;
import com.resultportal.exception.ResourceNotFoundException;
import com.resultportal.model.Result;
import com.resultportal.model.Student;
import com.resultportal.model.Subject;
import com.resultportal.repository.ResultRepository;
import com.resultportal.repository.StudentRepository;
import com.resultportal.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // ─── Grade Calculation ────────────────────────────────────────────

    public String calculateGrade(Double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B+";
        if (marks >= 60) return "B";
        if (marks >= 50) return "C";
        if (marks >= 40) return "D";
        return "F";
    }

    // ─── CRUD Operations ──────────────────────────────────────────────

    public List<ResultDTO> getAllResults() {
        return resultRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ResultDTO getResultById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result", "id", id));
        return toDTO(result);
    }

    public List<ResultDTO> getResultsByStudentId(Long studentId) {
        return resultRepository.findByStudentId(studentId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ResultDTO> getResultsByStudentIdAndSemester(Long studentId, Integer semester) {
        return resultRepository.findByStudentIdAndSemester(studentId, semester).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ResultDTO createResult(ResultDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", dto.getStudentId()));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subject", "id", dto.getSubjectId()));

        String grade = calculateGrade(dto.getMarks());

        Result result = Result.builder()
                .student(student)
                .subject(subject)
                .marks(dto.getMarks())
                .grade(grade)
                .semester(dto.getSemester())
                .build();

        Result saved = resultRepository.save(result);
        return toDTO(saved);
    }

    public ResultDTO updateResult(Long id, ResultDTO dto) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result", "id", id));

        if (dto.getMarks() != null) {
            result.setMarks(dto.getMarks());
            result.setGrade(calculateGrade(dto.getMarks()));
        }
        if (dto.getSemester() != null) {
            result.setSemester(dto.getSemester());
        }

        Result updated = resultRepository.save(result);
        return toDTO(updated);
    }

    public void deleteResult(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result", "id", id));
        resultRepository.delete(result);
    }

    public long getResultCount() {
        return resultRepository.count();
    }

    // ─── CSV Upload ───────────────────────────────────────────────────

    public List<ResultDTO> uploadCsv(MultipartFile file) {
        List<ResultDTO> results = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();

            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 4) continue;

                // CSV format: registrationNumber, subjectCode, marks, semester
                String regNumber = row[0].trim();
                String subjectCode = row[1].trim();
                Double marks = Double.parseDouble(row[2].trim());
                Integer semester = Integer.parseInt(row[3].trim());

                Student student = studentRepository.findByRegistrationNumber(regNumber)
                        .orElseThrow(() -> new BadRequestException(
                                "Student not found: " + regNumber));

                Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                        .orElseThrow(() -> new BadRequestException(
                                "Subject not found: " + subjectCode));

                String grade = calculateGrade(marks);

                Result result = Result.builder()
                        .student(student)
                        .subject(subject)
                        .marks(marks)
                        .grade(grade)
                        .semester(semester)
                        .build();

                Result saved = resultRepository.save(result);
                results.add(toDTO(saved));
            }
        } catch (IOException | CsvException e) {
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }

        return results;
    }

    // ─── PDF Generation ───────────────────────────────────────────────

    public byte[] generateMarksheetPdf(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student", "id", studentId));

        List<Result> results = resultRepository.findByStudentId(studentId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0, 51, 102));
            Paragraph title = new Paragraph("RESULT PORTAL - MARKSHEET", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Horizontal line
            document.add(new Paragraph("─".repeat(70)));

            // Student info
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Student Name: " + student.getName(), headerFont));
            document.add(new Paragraph("Registration No: " + student.getRegistrationNumber(),
                    normalFont));
            document.add(new Paragraph("Department: " + student.getDepartment(), normalFont));
            document.add(new Paragraph("Email: " + student.getEmail(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("─".repeat(70)));
            document.add(new Paragraph(" "));

            // Results table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3, 2, 2});

            // Table headers
            Font tableHeaderFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
            Color headerBg = new Color(0, 51, 102);

            String[] headers = {"#", "Subject Code", "Subject Name", "Marks", "Grade"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
                cell.setBackgroundColor(headerBg);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Table data
            double totalMarks = 0;
            int count = 0;
            for (int i = 0; i < results.size(); i++) {
                Result r = results.get(i);
                Color rowBg = (i % 2 == 0) ? new Color(240, 240, 240) : Color.WHITE;

                addCell(table, String.valueOf(i + 1), normalFont, rowBg);
                addCell(table, r.getSubject().getSubjectCode(), normalFont, rowBg);
                addCell(table, r.getSubject().getSubjectName(), normalFont, rowBg);
                addCell(table, String.format("%.1f", r.getMarks()), normalFont, rowBg);
                addCell(table, r.getGrade(), normalFont, rowBg);

                totalMarks += r.getMarks();
                count++;
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // Summary
            if (count > 0) {
                double avg = totalMarks / count;
                document.add(new Paragraph("Total Subjects: " + count, headerFont));
                document.add(new Paragraph(
                        String.format("Average Marks: %.2f", avg), headerFont));
                document.add(new Paragraph("Overall Grade: " + calculateGrade(avg), headerFont));
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph("─".repeat(70)));

            Font footerFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
            Paragraph footer = new Paragraph(
                    "This is a computer-generated marksheet. No signature required.",
                    footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

        } catch (DocumentException e) {
            throw new BadRequestException("Failed to generate PDF: " + e.getMessage());
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    // ─── Mapping helpers ──────────────────────────────────────────────

    private ResultDTO toDTO(Result result) {
        return ResultDTO.builder()
                .id(result.getId())
                .studentId(result.getStudent().getId())
                .studentName(result.getStudent().getName())
                .registrationNumber(result.getStudent().getRegistrationNumber())
                .subjectId(result.getSubject().getId())
                .subjectName(result.getSubject().getSubjectName())
                .subjectCode(result.getSubject().getSubjectCode())
                .marks(result.getMarks())
                .grade(result.getGrade())
                .semester(result.getSemester())
                .build();
    }
}
