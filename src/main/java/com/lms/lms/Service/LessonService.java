package com.lms.lms.Service;

import com.lms.lms.Entity.Lesson;
import com.lms.lms.Repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public List<Lesson> getLessonsByCourseId(String courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndex(courseId);
    }

    public Lesson getLessonById(String id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
    }

    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public String generateTranscript(String contentUrl) {
        // Mock transcript generation - in real implementation, this would call
        // a transcription service or AI service
        return "This is an auto-generated transcript for content at: " + contentUrl;
    }
}