package com.exam.serviceImpl;

import com.exam.entity.TestLink;
import com.exam.entity.TestType;
import com.exam.repository.testLinkRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TestLinkServiceImpl {

    @Autowired
    private testLinkRepository testInfoRepository;


    public TestLink createTest(TestLink testLink,String testType) {
        // Generate unique test ID
        testLink.setTestId(UUID.randomUUID().toString());
        testLink.setCreateDate(new Date());
        testLink.setTestType(testType);
        // Assuming timeDuration is in minutes
        if(StringUtils.isNotBlank(String.valueOf(testLink.getTimeDuration()))){
            testLink.setEndTime(testLink.getStartTime().plusMinutes(testLink.getTimeDuration()));
          }

        return testInfoRepository.save(testLink);
    }
}
