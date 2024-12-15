package com.exam.repository;

import com.exam.entity.TestLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TestLinkRepository extends JpaRepository<TestLink, Long> {
    Optional<TestLink> findById(Long testId);

    List<TestLink> findBytestType(String testType);

    @Query("SELECT t FROM TestLink t WHERE t.testType = :testType AND t.hideTestInfo = false")
    List<TestLink> findTestIdsByTestTypeAndHideTestInfoFalse(@Param("testType") String testType);

    @Modifying
    @Transactional
    @Query("UPDATE TestLink t SET t.hideTestInfo = :hideTestInfo WHERE t.id = :testId")
    int updateHideTestInfoByTestId(@Param("testId") Long testId, @Param("hideTestInfo") boolean hideTestInfo);

}

