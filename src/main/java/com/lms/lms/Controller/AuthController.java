
package com.lms.lms.Controller;

import com.lms.lms.Entity.User;
import com.lms.lms.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        String name = req.get("name");
        String email = req.get("email");
        String password = req.get("password");

        if (email == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("error",
                    Map.of("code", "FIELD_REQUIRED", "field", "email/password", "message", "Email and Password are required")));

        User user = userService.registerUser(name, email, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String password = req.get("password");

        if (email == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("error",
                    Map.of("code", "FIELD_REQUIRED", "field", "email/password", "message", "Email and Password are required")));

        User user = userService.login(email, password);
        return ResponseEntity.ok(Map.of("message", "Login successful", "user", user));
    }
}
