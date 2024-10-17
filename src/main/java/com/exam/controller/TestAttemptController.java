package com.exam.controller;

import com.exam.entity.TestAttempt;
import com.exam.serviceImpl.TestAttemptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attempt")
public class TestAttemptController {

    @Autowired
    private TestAttemptServiceImpl testAttemptService;

    @PostMapping("/user_attempt")
    public ResponseEntity<TestAttempt> userTestAttempt(@RequestBody TestAttempt testAttempt) {
        TestAttempt savedAttempt = testAttemptService.saveTestAttempt(testAttempt);
        return ResponseEntity.ok(savedAttempt);
    }
}
