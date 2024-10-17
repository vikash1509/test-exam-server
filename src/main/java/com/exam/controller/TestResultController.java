package com.exam.controller;

import com.exam.entity.TestResult;
import com.exam.serviceImpl.TestResultServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/test-results")
@RequiredArgsConstructor
public class TestResultController {

    @Autowired
    private  TestResultServiceImpl testResultService;


    @PostMapping("/upload")
    public ResponseEntity<InputStreamResource> uploadTestResults(
            @RequestParam("file") MultipartFile file,
            @RequestParam("testId") Long testId,
            @RequestParam("startTime") String startTime) {

        try {
            List<TestResult> results = testResultService.saveTestResults(file, testId, startTime);
            // Path to the generated CSV file
            File csvFile = testResultService.getOutputCSVFile();

            // Prepare response to return the CSV file
            InputStreamResource resource = new InputStreamResource(new FileInputStream(csvFile));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFile.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvFile.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/upload-updated")
    public ResponseEntity<List<TestResult>> uploadUpdatedTestResults(
            @RequestParam("file") MultipartFile file,
            @RequestParam("testId") Long testId,
            @RequestParam("startTime") String startTime) {

        try {
            // Call service to delete old results and save new ones
            List<TestResult> results = testResultService.saveUpdatedTestResults(file, testId, startTime);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/userResult")
    public ResponseEntity<List<TestResult>> getUserResults(
            @RequestParam("userId") String userId) {
        try {
            List<TestResult> results = testResultService.getTestResultsByUserId(userId);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {

            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/publishTestResult")
    public ResponseEntity<List<TestResult>> publishTestResults(
            @RequestParam("testId") Long testId) {
        try {
            List<TestResult> results = testResultService.getTestResultsByTestId(testId);
            if (results.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(results);
        } catch (Exception e) {

            return ResponseEntity.status(500).body(null);
        }
    }



}
