
package com.lms.lms.Controller;

import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.Lesson;
import com.lms.lms.Enums.CourseStatus;
import com.lms.lms.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin
public class CourseController {

    private final CourseService courseService;

    // GET /api/courses?limit=&offset=  → only published courses
    @GetMapping
    public ResponseEntity<?> getCourses(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        Page<Course> page = courseService.getPublishedCourses(limit, offset);
        // Sanitize payload to avoid serialization recursion and large graphs
        page.getContent().forEach(c -> {
            if (c.getCreator() != null) {
                c.getCreator().setCourses(null);
                c.getCreator().setEnrollments(null);
            }
        });
        int nextOffset = (page.hasNext()) ? offset + limit : -1;

        return ResponseEntity.ok(Map.of("items", page.getContent(), "next_offset", nextOffset));
    }

    // GET /api/courses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable String id) {
        Course course = courseService.getCourseById(id);
        if (course.getStatus() != CourseStatus.PUBLISHED)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", Map.of("code", "UNPUBLISHED", "message", "Course not available")));

        return ResponseEntity.ok(course);
    }

    // POST /api/courses (Creator only) — Idempotency required
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course courseData,
                                          @RequestHeader("X-User-Id") String creatorId,
                                          @RequestHeader("Idempotency-Key") String idempotencyKey,
                                          HttpServletRequest request) {

        // If idempotent resource exists, return it
        String existingId = (String) request.getAttribute("idempotentResourceId");
        if (existingId != null)
            return ResponseEntity.ok(courseService.getCourseById(existingId));

        Course created = courseService.createCourse(creatorId, courseData, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

