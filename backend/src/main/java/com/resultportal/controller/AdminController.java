package com.resultportal.controller;
import com.resultportal.dto.ApiResponse;
import com.resultportal.dto.ResultDTO;
import com.resultportal.service.ResultService;
import com.resultportal.service.StudentService;
import com.resultportal.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;

    @PostMapping("/upload-csv")
    public ResponseEntity<ApiResponse> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please select a CSV file to upload"));
        }

        List<ResultDTO> results = resultService.uploadCsv(file);
        return ResponseEntity.ok(
                ApiResponse.success(results.size() + " results uploaded successfully", results));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentService.getStudentCount());
        stats.put("totalSubjects", subjectService.getSubjectCount());
        stats.put("totalResults", resultService.getResultCount());
        return ResponseEntity.ok(stats);
    }
}
