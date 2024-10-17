package com.exam.controller;

import com.exam.entity.TestLink;
import com.exam.entity.TestType;
import com.exam.serviceImpl.TestLinkServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
public class TestLinkController {

    @Autowired
    private TestLinkServiceImpl testInfoService;

    @PostMapping("/create")
    public ResponseEntity<TestLink> createTest(@RequestBody TestLink testInfo) {
        TestLink createdTest = testInfoService.createTest(testInfo, TestType.RankBooster.name());
        return ResponseEntity.ok(createdTest);
    }

    @PostMapping("/create/practiceTest")
    public ResponseEntity<TestLink> createPracticeTest(@RequestBody TestLink testInfo) {
        TestLink createdTest = testInfoService.createTest(testInfo, TestType.PRACTICE.name());
        return ResponseEntity.ok(createdTest);
    }
}

