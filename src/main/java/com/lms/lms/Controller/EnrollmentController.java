package com.lms.lms.Controller;

import com.lms.lms.Entity.Enrollment;
import com.lms.lms.Service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@CrossOrigin
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // POST /api/enrollments (Learner enrolls in course) — Idempotency required
    @PostMapping
    public ResponseEntity<?> enrollInCourse(@RequestBody Map<String, String> request,
                                           @RequestHeader("X-User-Id") String learnerId,
                                           @RequestHeader("Idempotency-Key") String idempotencyKey,
                                           HttpServletRequest httpRequest) {

        String courseId = request.get("courseId");
        if (courseId == null) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    Map.of("code", "FIELD_REQUIRED", "field", "courseId", "message", "Course ID is required")));
        }

        // If idempotent resource exists, return it
        String existingId = (String) httpRequest.getAttribute("idempotentResourceId");
        if (existingId != null) {
            return ResponseEntity.ok(enrollmentService.getEnrollmentById(existingId));
        }

        Enrollment enrollment = enrollmentService.enrollInCourse(learnerId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    // POST /api/enrollments/{id}/complete-lesson
    @PostMapping("/{id}/complete-lesson")
    public ResponseEntity<?> completeLesson(@PathVariable String id, @RequestBody Map<String, String> request) {
        String lessonId = request.get("lessonId");
        if (lessonId == null) {
            return ResponseEntity.badRequest().body(Map.of("error",
                    Map.of("code", "FIELD_REQUIRED", "field", "lessonId", "message", "Lesson ID is required")));
        }

        Enrollment enrollment = enrollmentService.completeLesson(id, lessonId);
        return ResponseEntity.ok(enrollment);
    }

    // GET /api/enrollments/progress — Get user's progress
    @GetMapping("/progress")
    public ResponseEntity<?> getProgress(@RequestHeader("X-User-Id") String userId) {
        List<Map<String, Object>> progress = enrollmentService.getUserProgress(userId);
        return ResponseEntity.ok(Map.of("progress", progress));
    }
}