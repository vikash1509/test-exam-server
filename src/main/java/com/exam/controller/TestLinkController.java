package com.exam.controller;

import com.exam.entity.TestLink;
import com.exam.serviceImpl.TestLinkServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestLinkController {
    @Autowired
    private TestLinkServiceImpl testInfoService;

    @PostMapping("/create")
    public ResponseEntity<TestLink> createTest(@RequestBody TestLink testInfo) {
        TestLink createdTest = testInfoService.createTest(testInfo);
        if(null != createdTest ) {
            return ResponseEntity.ok(createdTest);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PutMapping("/hide-test-info")
    public String updateHideTestInfo(@RequestParam Long testId, @RequestParam boolean hideTestInfo) {
        boolean isUpdated = testInfoService.updateHideTestInfo(testId, hideTestInfo);
        return isUpdated ? "Update successful" : "Update failed";
    }
    @GetMapping("/showLiveTest")
    public List<TestLink> showLiveTest() {
        return testInfoService.getTestLink("RankBooster");
    }

    @GetMapping("/showPracticeTest")
    public List<TestLink> showPracticeTest() {
        return testInfoService.getTestLink("Practice");
    }

    @GetMapping("/showAllLiveTest")
    public List<TestLink> showAllLiveTest() {
        return testInfoService.getAllTestLink("RankBooster");
    }

    @GetMapping("/showAllPracticeTest")
    public List<TestLink> showAllPracticeTest() {
        return testInfoService.getAllTestLink("Practice");
    }

    @PutMapping("/publish_result")
    public String publishResult(@RequestParam Long testId, @RequestParam boolean resultPublish) {
        boolean isUpdated = testInfoService.publishResult(testId, resultPublish);
        return isUpdated ? "Update successful" : "Update failed";
    }
}

