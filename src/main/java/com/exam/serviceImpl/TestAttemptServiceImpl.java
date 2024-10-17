package com.exam.serviceImpl;

import com.exam.entity.TestAttempt;
import com.exam.repository.TestAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TestAttemptServiceImpl {

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    public TestAttempt saveTestAttempt(TestAttempt testAttempt) {
        testAttempt.setStartTime(new Date());
        return testAttemptRepository.save(testAttempt);
    }
}
