package com.lms.lms.Repository;

import com.lms.lms.Entity.Course;
import com.lms.lms.Enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    @Query("SELECT c FROM Course c WHERE c.status = :status")
    Page<Course> findByStatusWithDetails(CourseStatus status, Pageable pageable);
    
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    List<Course> findByStatusIn(List<CourseStatus> statuses);
}