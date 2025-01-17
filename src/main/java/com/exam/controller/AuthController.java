package com.exam.controller;

import com.exam.entity.User;
import com.exam.serviceImpl.UserServiceImpl;
import com.exam.util.GoogleTokenVerifier;
import com.exam.util.JwtTokenUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @GetMapping("/login_user")
    public ResponseEntity<?> loginUser(@RequestParam String userEmail,
                                       @RequestParam String userPass) {
        logger.info("Received login request for email: {}", userEmail);
        try {
            User user = userService.loginUser(userEmail, userPass);
            String token = jwtTokenUtil.generateToken(user.getUserMailId(), user.getUserType(), user.getUserId());
            logger.info("Login successful for user: {}", userEmail);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            logger.error("Login failed for user: {}, error: {}", userEmail, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/google_login")
    public ResponseEntity<?> googleLogin(@RequestParam String token) {
        logger.info("Received Google login request with token.");
        try {
            // Verify the Google token
            logger.info("token is: {}", token);
            GoogleIdToken.Payload payload = googleTokenVerifier.verifyToken(token);
            if (payload == null) {
                logger.warn("Invalid Google token received.");
                throw new Exception("Invalid Google token.");
            }

            // Extract user information from the Google token
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            logger.info("Google token verified successfully for email: {}", email);

            // Handle user creation or retrieval
            User user = userService.handleGoogleUser(email, name);
            logger.info("Google user handled successfully: {}", email);

            // Generate JWT token
            String jwtToken = jwtTokenUtil.generateToken(user.getUserMailId(), user.getUserType(), user.getUserId());
            logger.info("JWT token generated successfully for Google user: {}", email);
            return ResponseEntity.ok(Map.of("token", jwtToken));
        } catch (Exception e) {
            logger.error("Google login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
