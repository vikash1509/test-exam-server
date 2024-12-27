package com.exam.controller;

import com.exam.entity.Role;
import com.exam.entity.User;
import com.exam.entity.UserRole;
import com.exam.serviceImpl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        long startTime = System.currentTimeMillis(); // Start time measurement
        try {
            Set<UserRole> userRoles = new HashSet<>();
//            UserRole  userRole = new UserRole();
//            userRole.setRole(new Role(41L,"Normal"));
//            userRole.setUser(user);
//            userRoles.add(userRole);
            User createdUser = userService.createUser(user,userRoles);
            logger.info("User created successfully: {}", createdUser.getUserName());

            long endTime = System.currentTimeMillis(); // End time measurement
            logger.info("createUser API executed in {} ms", (endTime - startTime)); // Log execution time

            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis(); // End time measurement for error case
            logger.error("Error while creating user: {}. API executed in {} ms", e.getMessage(), (endTime - startTime));

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // API Endpoint to trigger Selenium automation with a dynamic username
    @GetMapping("/automate-quiz")
    public String automateQuiz(@RequestParam String username,
                               @RequestParam String userId,
                               @RequestParam String testId) {

        // Call the automation method with the passed username
      return  userService.automateQuizForm(username,userId,testId);

    }

    @GetMapping("/login_user")
    public ResponseEntity<?> loginUser(@RequestParam String userNameOrEmail,
                               @RequestParam String userPass ) {
        try{
            return ResponseEntity.ok(userService.loginUser(userNameOrEmail,userPass));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        userService.verifyOtp(email, otp);
        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        userService.resetPassword(email, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUser(@RequestParam String userId) {
        try{
            return ResponseEntity.ok(userService.getUser(userId));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
