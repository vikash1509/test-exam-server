package com.exam.repository;

import com.exam.entity.TestResult;
import com.exam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId); // Check if userId exists
    @Query("SELECT ur FROM User ur WHERE ur.userName = :userName OR ur.userMailId = :userMailId")
    Optional<User> findByUsernameOrEmail(@Param("userName") String userName ,@Param("userMailId") String userMailId);
    Optional<User> findByUserMailId(String userMailId);
    Optional<User> findByUserRollNo(String rollNo);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userLastLogin = :lastLogin WHERE u.userId = :userId")
    int updateUserLastLogin(@Param("userId") String userId, @Param("lastLogin") LocalDateTime lastLogin);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userRating = :userRating WHERE u.userId = :userId")
    int updateUserRating(@Param("userId") String userId, @Param("userRating") int userRating);


    @Query("SELECT u FROM User u WHERE u.userMailId = :email AND u.userPassword = :password")
    Optional<User> findByUserMailIdAndPassword(@Param("email") String email, @Param("password") String password);

}
