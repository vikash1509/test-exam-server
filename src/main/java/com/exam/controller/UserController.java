package com.exam.controller;

//import com.exam.entity.Role;
import com.exam.entity.User;
//import com.exam.entity.UserRole;
import com.exam.serviceImpl.UserServiceImpl;
import com.exam.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        long startTime = System.currentTimeMillis(); // Start time measurement
        try {
//            Set<UserRole> userRoles = new HashSet<>();
//            UserRole  userRole = new UserRole();
//            userRole.setRole(new Role(41L,"Normal"));
//            userRole.setUser(user);
//            userRoles.add(userRole);
//            User createdUser = userService.createUser(user,userRoles);
            User createdUser = userService.createUser(user);
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


    @GetMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String userEmail) {
        try {
            userService.generateAndSendOtp(userEmail);
            return ResponseEntity.ok("OTP sent to your email.");
        }catch (Exception e){
           return ResponseEntity.badRequest().body(String.valueOf(e));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try{
            userService.verifyOtp(email, otp);
            return ResponseEntity.ok("OTP verified successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(String.valueOf(e));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try{
            userService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password updated successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(String.valueOf(e));
        }

    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            // Extract the token from the Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
            }

            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

            // Decode or validate the token (e.g., using a library like jjwt or auth0)
            String userId = jwtTokenUtil.getClaimFromToken(token,"userId");

            // Fetch user details using the userId
            return ResponseEntity.ok(userService.getUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-all-user")
    public ResponseEntity<?> getAllUser(HttpServletRequest request) {
        try {
            // Extract the token from the Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
            }

            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

            // Decode or validate the token (e.g., using a library like jjwt or auth0)
            String userId = jwtTokenUtil.getClaimFromToken(token,"userId");

            // Fetch user details using the userId
            return ResponseEntity.ok(userService.getallUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password-otp")
    public ResponseEntity<String> resetPasswordWithOtp(@RequestParam String userEmail, @RequestParam String newPassword,@RequestParam String otp) {
        try{
            userService.verifyOtp(userEmail, otp);
            logger.info("OTP Verified successfully : ");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(String.valueOf(e));
        }

        try{
            userService.resetPassword(userEmail, newPassword);
            return ResponseEntity.ok("Password updated successfully.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(String.valueOf(e));
        }

    }

}
