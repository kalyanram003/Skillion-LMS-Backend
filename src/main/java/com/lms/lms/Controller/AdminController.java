
package com.lms.lms.Controller;

import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.User;
import com.lms.lms.Service.CourseService;
import com.lms.lms.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;

    // GET /admin/review/courses → all draft/pending courses
    @GetMapping("/review/courses")
    public ResponseEntity<?> getCoursesForReview() {
        List<Course> draftCourses = courseService.getPendingCourses();
        return ResponseEntity.ok(Map.of("items", draftCourses));
    }

    // PATCH /admin/review/courses/{id}/approve
    @PatchMapping("/review/courses/{id}/approve")
    public ResponseEntity<?> approveCourse(@PathVariable String id) {
        Course course = courseService.publishCourse(id);
        return ResponseEntity.ok(Map.of("message", "Course published", "course", course));
    }

    // PATCH /admin/review/creators/{id}/approve
    @PatchMapping("/review/creators/{id}/approve")
    public ResponseEntity<?> approveCreator(@PathVariable String id) {
        User user = userService.approveCreatorApplication(id);
        return ResponseEntity.ok(Map.of("message", "Creator approved", "user", user));
    }
}
