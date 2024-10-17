package com.exam.serviceImpl;

import com.exam.entity.User;
import com.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User newUser) throws Exception {
        // Check if the userId already exists
        Optional<User> existingUser = userRepository.findById(newUser.getUserId());

        if (existingUser.isPresent()) {
            throw new Exception("User with userId " + newUser.getUserId() + " already exists.");
        }

        // Set default user rating and rank
        newUser.setUserRating(1000); // Default rating
        newUser.setUserRank(0);      // Default rank can be 0

        return userRepository.save(newUser);
    }
}
