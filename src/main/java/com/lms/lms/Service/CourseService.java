package com.lms.lms.Service;

import com.lms.lms.Entity.Course;
import com.lms.lms.Entity.User;
import com.lms.lms.Enums.CourseStatus;
import com.lms.lms.Repository.CourseRepository;
import com.lms.lms.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final IdempotencyService idempotencyService;

    public Page<Course> getPublishedCourses(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return courseRepository.findByStatusWithDetails(CourseStatus.PUBLISHED, pageable);
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    public Course createCourse(String creatorId, Course courseData, String idempotencyKey) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        
        Course course = new Course();
        course.setTitle(courseData.getTitle());
        course.setDescription(courseData.getDescription());
        course.setStatus(CourseStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        course.setCreator(creator);
        
        Course saved = courseRepository.save(course);
        idempotencyService.put(idempotencyKey, saved.getId());
        return saved;
    }

    public List<Course> getPendingCourses() {
        return courseRepository.findByStatusIn(Arrays.asList(CourseStatus.DRAFT));
    }

    public Course publishCourse(String courseId) {
        Course course = getCourseById(courseId);
        course.setStatus(CourseStatus.PUBLISHED);
        return courseRepository.save(course);
    }
}