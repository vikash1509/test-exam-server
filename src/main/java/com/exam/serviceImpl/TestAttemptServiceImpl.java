package com.exam.serviceImpl;

import com.exam.entity.TestAttempt;
import com.exam.repository.TestAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TestAttemptServiceImpl {

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    public TestAttempt saveTestAttempt(TestAttempt newTestAttempt) {
        newTestAttempt.setStartTime(new Date());


        Optional<TestAttempt> existingAttempt = testAttemptRepository
                .findByTestIdAndUserId(newTestAttempt.getTestId(), newTestAttempt.getUserId());

        if (existingAttempt.isPresent()) {
            TestAttempt attempt = existingAttempt.get();
            attempt.setAttemptCount(attempt.getAttemptCount() + 1); // Increment attempt count
//            attempt.setStartTime(newTestAttempt.getStartTime()); // Update other fields
//            attempt.setRating(newTestAttempt.getRating());
//            attempt.setFeedback(newTestAttempt.getFeedback());
            return testAttemptRepository.save(attempt);
        } else {
            return testAttemptRepository.save(newTestAttempt);
        }    }
}
