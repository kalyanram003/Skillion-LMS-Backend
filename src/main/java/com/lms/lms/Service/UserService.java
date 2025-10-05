
package com.lms.lms.Service;

import com.lms.lms.Entity.User;
import com.lms.lms.Enums.ApplicationStatus;
import com.lms.lms.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(com.lms.lms.Enums.UserRole.LEARNER);
        user.setCreatorApplicationStatus(ApplicationStatus.PENDING);
        
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt.get();
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    // Creator applies to become a course creator
    public User applyForCreator(String userId, String bio, String portfolioUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setCreatorBio(bio);
        user.setCreatorPortfolioUrl(portfolioUrl);
        user.setCreatorApplicationStatus(ApplicationStatus.PENDING);
        return userRepository.save(user);
    }

    // Admin approves creator application
    public User approveCreatorApplication(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setCreatorApplicationStatus(ApplicationStatus.APPROVED);
        user.setRole(com.lms.lms.Enums.UserRole.CREATOR);
        return userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateUser(String userId, String name, String email) {
        User user = getUserById(userId);
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        return userRepository.save(user);
    }
}
