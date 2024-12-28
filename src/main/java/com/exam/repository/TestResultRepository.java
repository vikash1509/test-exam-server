package com.exam.repository;

import com.exam.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    // Custom queries if needed
    @Query("SELECT tr FROM TestResult tr WHERE tr.userId = :userId")
    List<TestResult> findByUserId(@Param("userId") String userId);

    void deleteByTestId(String testId);

    List<TestResult> findByTestId(String testId);

    @Query("SELECT tr FROM TestResult tr WHERE tr.userId = :userId AND tr.testId = :testId")
    List<TestResult> findByUserIdAndTestId(@Param("userId") String userId, @Param("testId") Long testId);

    @Query("SELECT COUNT(t) > 0 FROM TestResult t WHERE t.userRollNo = :userRollNo AND t.submittedTime = :submittedTime")
    boolean existsByUserRollNoAndSubmittedTime(@Param("userRollNo") String userRollNo, @Param("submittedTime") String submittedTime);

    @Query("SELECT COUNT(t) > 0 FROM TestResult t WHERE t.answerSheetLink = :answerSheetLink")
    boolean existsByAnswerSheetLink(@Param("answerSheetLink") String answerSheetLink);

}
