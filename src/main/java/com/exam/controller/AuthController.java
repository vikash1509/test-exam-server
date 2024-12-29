package com.exam.controller;

import com.exam.entity.User;
import com.exam.serviceImpl.UserServiceImpl;
import com.exam.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping("/login_user")
    public ResponseEntity<?> loginUser(@RequestParam String userEmail,
                                       @RequestParam String userPass ) {
        try{
            User user = userService.loginUser(userEmail, userPass);
            String token = jwtTokenUtil.generateToken(user.getUserMailId());
            return ResponseEntity.ok(Map.of("token", token));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
