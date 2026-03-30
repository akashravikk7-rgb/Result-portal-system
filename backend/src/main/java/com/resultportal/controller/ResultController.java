package com.resultportal.controller;

import com.resultportal.dto.ApiResponse;
import com.resultportal.dto.ResultDTO;
import com.resultportal.service.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping
    public ResponseEntity<List<ResultDTO>> getAllResults() {
        return ResponseEntity.ok(resultService.getAllResults());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDTO> getResultById(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getResultById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ResultDTO>> getResultsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(resultService.getResultsByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<ResultDTO>> getResultsByStudentAndSemester(
            @PathVariable Long studentId, @PathVariable Integer semester) {
        return ResponseEntity.ok(
                resultService.getResultsByStudentIdAndSemester(studentId, semester));
    }

    @PostMapping
    public ResponseEntity<ResultDTO> createResult(@Valid @RequestBody ResultDTO resultDTO) {
        return ResponseEntity.ok(resultService.createResult(resultDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDTO> updateResult(
            @PathVariable Long id, @Valid @RequestBody ResultDTO resultDTO) {
        return ResponseEntity.ok(resultService.updateResult(id, resultDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.ok(ApiResponse.success("Result deleted successfully"));
    }

    @GetMapping("/download-pdf/{studentId}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long studentId) {
        byte[] pdfBytes = resultService.generateMarksheetPdf(studentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=marksheet_" + studentId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
