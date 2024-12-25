package com.exam.controller;

import com.exam.entity.TestResult;
import com.exam.serviceImpl.TestResultServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/test-results")
@RequiredArgsConstructor
public class TestResultController {

    @Autowired
    private  TestResultServiceImpl testResultService;


    private static final Logger logger = LoggerFactory.getLogger(TestResultController.class);

    @PostMapping("/upload")
    public ResponseEntity<InputStreamResource> uploadTestResults(
            @RequestParam("file") MultipartFile file,
            @RequestParam("testId") Long testId) {

        long startTime = System.currentTimeMillis();
        try {
            List<TestResult> results = testResultService.saveTestResults(file, testId);
            File csvFile = testResultService.getOutputCSVFile();

            InputStreamResource resource = new InputStreamResource(new FileInputStream(csvFile));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFile.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error in uploadTestResults: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for uploadTestResults: {} ms", (endTime - startTime));
        }
    }

    @PostMapping("/upload-updated")
    public ResponseEntity<List<TestResult>> uploadUpdatedTestResults(
            @RequestParam("file") MultipartFile file,
            @RequestParam("testId") Long testId) {

        long startTime = System.currentTimeMillis();
        try {
            List<TestResult> results = testResultService.saveUpdatedTestResults(file, testId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error in uploadUpdatedTestResults: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for uploadUpdatedTestResults: {} ms", (endTime - startTime));
        }
    }

    @GetMapping("/userResult")
    public ResponseEntity<List<TestResult>> getUserResults(
            @RequestParam("userId") String userId) {

        long startTime = System.currentTimeMillis();
        try {
            List<TestResult> results = testResultService.getTestResultsByUserId(userId);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error in getUserResults: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for getUserResults: {} ms", (endTime - startTime));
        }
    }

    @GetMapping("/showTestResult")
    public ResponseEntity<?> showTestResult(
            @RequestParam("testId") Long testId) {

        long startTime = System.currentTimeMillis();
        try {
            List<TestResult> results = testResultService.getTestResultsByTestId(testId, false);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error in showTestResult: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while fetching user results.");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for showTestResult: {} ms", (endTime - startTime));
        }
    }

    @GetMapping("/showTestResultFromDb")
    public ResponseEntity<?> showTestResultFromDb(
            @RequestParam("testId") Long testId) {

        long startTime = System.currentTimeMillis();
        try {
            List<TestResult> results = testResultService.getTestResultsByTestId(testId, true);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error in showTestResultFromDb: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while fetching user results.");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for showTestResultFromDb: {} ms", (endTime - startTime));
        }
    }

    @PostMapping("/updateUserRatings/{testId}")
    public ResponseEntity<String> updateUserRatings(@PathVariable Long testId) {

        long startTime = System.currentTimeMillis();
        try {
            testResultService.updateUserRatings(testId);
            return ResponseEntity.ok("User ratings updated successfully!");
        } catch (Exception e) {
            logger.error("Error in updateUserRatings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Execution time for updateUserRatings: {} ms", (endTime - startTime));
        }
    }
}
