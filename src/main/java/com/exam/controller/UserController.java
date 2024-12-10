package com.exam.controller;

import com.exam.entity.Role;
import com.exam.entity.User;
import com.exam.entity.UserRole;
import com.exam.serviceImpl.UserServiceImpl;
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

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            Set<UserRole> userRoles = new HashSet<>();
            UserRole  userRole = new UserRole();
            userRole.setRole(new Role(41L,"Normal"));
            userRole.setUser(user);
            userRoles.add(userRole);

            User createdUser = userService.createUser(user,userRoles);

            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API Endpoint to trigger Selenium automation with a dynamic username
    @GetMapping("/automate-quiz")
    public String automateQuiz(@RequestParam String username,
                               @RequestParam String userId,
                               @RequestParam Long testId) {

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

}
