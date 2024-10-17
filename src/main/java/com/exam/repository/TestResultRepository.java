package com.exam.repository;

import com.exam.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    // Custom queries if needed
    List<TestResult> findByUserId(String userId);

    void deleteByTestId(Long testId);

    List<TestResult> findByTestId(Long testId);
}
