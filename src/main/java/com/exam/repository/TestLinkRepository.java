package com.exam.repository;

import com.exam.entity.TestLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestLinkRepository extends JpaRepository<TestLink, Long> {
    Optional<TestLink> findById(Long testId);
}
