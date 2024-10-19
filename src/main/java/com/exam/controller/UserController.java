package com.exam.controller;

import com.exam.entity.User;
import com.exam.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
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

}
