package com.exam.repository;

import com.exam.entity.TestResult;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String userId); // Check if userId exists
    @Query("SELECT ur FROM User ur WHERE ur.userName = :userName OR ur.userMailId = :userMailId")
    Optional<User> findByUsernameOrEmail(@Param("userName") String userName ,@Param("userMailId") String userMailId);

}
