package com.exam.serviceImpl;

import com.exam.entity.TestLink;
import com.exam.entity.TestType;
import com.exam.repository.TestLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class TestLinkServiceImpl {
    @Autowired
    private TestLinkRepository testInfoRepository;

    public TestLink createTest(TestLink testLink) {
        // Generate unique test ID
        testLink.setCreateDate(new Date());

        // Check if the start time is after the current time
        if (testLink.getStartTime() == null || (testLink.getStartTime() != null && testLink.getStartTime().isBefore(LocalDateTime.now()))) {
            System.out.println("Test start time must be in the future.");
            return null;
        }
        // Check if end time is at least 5 minutes after start time
        if (testLink.getEndTime() != null) {
            long startTimeInMillis = testLink.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endTimeInMillis = testLink.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long diffInMinutes = (endTimeInMillis - startTimeInMillis) / (1000 * 60); // Convert milliseconds to minutes
            if (diffInMinutes < 5) {
                System.out.println("Test end time must be at least 5 minutes after start time.");
                return null;
            }
        }
        // Existing conditions for test type and test link
        if ((TestType.RankBooster.name().equalsIgnoreCase(testLink.getTestType())
                || TestType.PRACTICE.name().equalsIgnoreCase(testLink.getTestType()))
                && testLink.getTestLink().startsWith("https://quizzory.in/")) {
            return testInfoRepository.save(testLink);
        }
        System.out.println("createTest returning null");
        return null;
    }

    public List<TestLink> getTestLink(String testType){
        return testInfoRepository.findTestIdsByTestTypeAndHideTestInfoFalse(testType);
    }

    public boolean updateHideTestInfo(Long testId, boolean hideTestInfo) {
        int rowsUpdated = testInfoRepository.updateHideTestInfoByTestId(testId, hideTestInfo);
        return rowsUpdated > 0; // Return true if at least one row was updated
    }

}
