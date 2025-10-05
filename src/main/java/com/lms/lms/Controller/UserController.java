package com.lms.lms.Controller;

import com.lms.lms.Entity.User;
import com.lms.lms.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    @PostMapping("/apply-creator")
    public ResponseEntity<?> applyAsCreator(@RequestBody Map<String, String> req,
                                           @RequestHeader("X-User-Id") String userId) {
        String bio = req.get("bio");
        String portfolioUrl = req.get("portfolioUrl");

        if (bio == null || portfolioUrl == null) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    Map.of("code", "FIELD_REQUIRED", "field", "bio/portfolioUrl", "message", "Bio and Portfolio URL are required")));
        }

        User user = userService.applyForCreator(userId, bio, portfolioUrl);
        return ResponseEntity.ok(Map.of("message", "Creator application submitted successfully", "user", user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("X-User-Id") String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, String> req) {
        String name = req.get("name");
        String email = req.get("email");
        
        User user = userService.updateUser(id, name, email);
        return ResponseEntity.ok(user);
    }
}
