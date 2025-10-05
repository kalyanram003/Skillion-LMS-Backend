package com.lms.lms.Service;

import com.lms.lms.Entity.Enrollment;
import com.lms.lms.Enums.EnrollmentStatus;
import com.lms.lms.Repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment enrollInCourse(String learnerId, String courseId) {
        // Check if already enrolled
        Optional<Enrollment> existing = enrollmentRepository.findByLearnerIdAndCourseId(learnerId, courseId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        // Set learner and course - you'll need to inject repositories for this
        
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment completeLesson(String enrollmentId, String lessonId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollment.getCompletedLessonIds().add(lessonId);
        
        // Check if all lessons completed and issue certificate
        // This logic would need course and lesson data
        
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(String id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
    }

    public String generateCertificateSerialHash(String enrollmentId) {
        try {
            String input = enrollmentId + LocalDateTime.now().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate hash", e);
        }
    }

    public List<Map<String, Object>> getUserProgress(String userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByLearnerId(userId);
        
        return enrollments.stream().map(enrollment -> {
            Map<String, Object> progress = new HashMap<>();
            progress.put("enrollmentId", enrollment.getId());
            progress.put("courseId", enrollment.getCourse().getId());
            progress.put("courseTitle", enrollment.getCourse().getTitle());
            progress.put("status", enrollment.getStatus());
            progress.put("completedLessons", enrollment.getCompletedLessonIds().size());
            progress.put("totalLessons", enrollment.getCourse().getLessons().size());
            progress.put("progressPercentage", calculateProgressPercentage(enrollment));
            progress.put("certificateSerialHash", enrollment.getCertificateSerialHash());
            return progress;
        }).collect(Collectors.toList());
    }

    private int calculateProgressPercentage(Enrollment enrollment) {
        int totalLessons = enrollment.getCourse().getLessons().size();
        if (totalLessons == 0) return 0;
        return (enrollment.getCompletedLessonIds().size() * 100) / totalLessons;
    }
}