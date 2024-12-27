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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestLinkController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createTest(@RequestBody TestLink testInfo) {
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting createTest method");
            TestLink createdTest = testInfoService.createTest(testInfo);
            logger.info("createTest executed successfully in {} ms", System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(createdTest);
        } catch (Exception e) {
            logger.error("Error in createTest: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/hide-test-info")
    public String updateHideTestInfo(@RequestParam String testId, @RequestParam boolean hideTestInfo) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting updateHideTestInfo method for testId: {}", testId);
        boolean isUpdated = testInfoService.updateHideTestInfo(testId, hideTestInfo);
        logger.info("updateHideTestInfo executed successfully in {} ms", System.currentTimeMillis() - startTime);
        return isUpdated ? "Update successful" : "Update failed";
    }

    @GetMapping("/showLiveTest")
    public List<TestLink> showLiveTest() {
        long startTime = System.currentTimeMillis();
        logger.info("Starting showLiveTest method");
        List<TestLink> liveTests = testInfoService.getTestLink("RankBooster");
        logger.info("showLiveTest executed successfully in {} ms", System.currentTimeMillis() - startTime);
        return liveTests;
    }

    @GetMapping("/showPracticeTest")
    public List<TestLink> showPracticeTest() {
        long startTime = System.currentTimeMillis();
        logger.info("Starting showPracticeTest method");
        List<TestLink> practiceTests = testInfoService.getTestLink("Practice");
        logger.info("showPracticeTest executed successfully in {} ms", System.currentTimeMillis() - startTime);
        return practiceTests;
    }

    @GetMapping("/showAllLiveTest")
    public List<TestLink> showAllLiveTest() {
        long startTime = System.currentTimeMillis();
        logger.info("Starting showAllLiveTest method");
        List<TestLink> allLiveTests = testInfoService.getAllTestLink("RankBooster");
        logger.info("showAllLiveTest executed successfully in {} ms", System.currentTimeMillis() - startTime);
        return allLiveTests;
    }

    @GetMapping("/showAllPracticeTest")
    public List<TestLink> showAllPracticeTest() {
        long startTime = System.currentTimeMillis();
        logger.info("Starting showAllPracticeTest method");
        List<TestLink> allPracticeTests = testInfoService.getAllTestLink("Practice");
        logger.info("showAllPracticeTest executed successfully in {} ms", System.currentTimeMillis() - startTime);
        return allPracticeTests;
    }

    @PutMapping("/publish_result")
    public String publishResult(@RequestParam String testId, @RequestParam boolean resultPublish) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting publishResult method for testId: {}", testId);
        boolean isUpdated = testInfoService.publishResult(testId, resultPublish);
        logger.info("publishResult executed successfully in {} ms", System.currentTimeMillis() - startTime);
        return isUpdated ? "Update successful" : "Update failed";
    }
}