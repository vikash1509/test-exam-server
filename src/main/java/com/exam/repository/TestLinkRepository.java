package com.exam.repository;

import com.exam.entity.TestLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestLinkRepository extends JpaRepository<TestLink, String> {
    Optional<TestLink> findById(String testId);

    List<TestLink> findBytestType(String testType);

    @Query("SELECT t FROM TestLink t WHERE t.testType = :testType AND t.hideTestInfo = false")
    List<TestLink> findTestIdsByTestTypeAndHideTestInfoFalse(@Param("testType") String testType);

    @Modifying
    @Transactional
    @Query("UPDATE TestLink t SET t.hideTestInfo = :hideTestInfo WHERE t.id = :testId")
    int updateHideTestInfoByTestId(@Param("testId") String testId, @Param("hideTestInfo") boolean hideTestInfo);

    @Modifying
    @Transactional
    @Query("UPDATE TestLink t SET t.resultFileUploaded = :resultFileUploaded WHERE t.id = :testId")
    int updateResultFileUplodedByTestId(@Param("testId") String testId, @Param("resultFileUploaded") boolean resultFileUploaded);


    @Modifying
    @Transactional
    @Query("UPDATE TestLink t SET t.resultPublish = :resultPublish WHERE t.id = :testId")
    int updateResultPublishByTestId(@Param("testId") String testId, @Param("resultPublish") boolean hideTestInfo);

}

