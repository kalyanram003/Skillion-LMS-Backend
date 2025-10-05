package com.lms.lms.Controller;

import com.lms.lms.Entity.Lesson;
import com.lms.lms.Service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@CrossOrigin
public class LessonController {

    private final LessonService lessonService;

    // GET /api/lessons/course/{courseId}
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getLessonsByCourse(@PathVariable String courseId) {
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(Map.of("items", lessons));
    }

    // GET /api/lessons/{id} — Get specific lesson
    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(@PathVariable String id) {
        Lesson lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    // POST /api/lessons — Create lesson (Creator only) — Idempotency required
    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody Lesson lesson,
                                         @RequestHeader("X-User-Id") String creatorId,
                                         @RequestHeader("Idempotency-Key") String idempotencyKey) {
        // Auto-generate transcript
        String transcript = lessonService.generateTranscript(lesson.getContentUrl());
        lesson.setTranscript(transcript);
        
        Lesson created = lessonService.createLesson(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}