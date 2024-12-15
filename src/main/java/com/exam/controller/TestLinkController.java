package com.exam.controller;

import com.exam.entity.TestLink;
import com.exam.entity.TestType;
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

    @PutMapping("/{testId}/hide-test-info")
    public String updateHideTestInfo(@PathVariable Long testId, @RequestParam boolean hideTestInfo) {
        boolean isUpdated = testInfoService.updateHideTestInfo(testId, hideTestInfo);
        return isUpdated ? "Update successful" : "Update failed";
    }
    @GetMapping("/showLiveTest")
    public List<TestLink> showLiveTest() {
        return testInfoService.getTestLink("RankBooster");
    }

    @GetMapping("/showPracticeTests")
    public List<TestLink> showPracticeTest() {
        return testInfoService.getTestLink("Practice");
    }


}

