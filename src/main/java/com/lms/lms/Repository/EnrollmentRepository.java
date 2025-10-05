package com.lms.lms.Repository;

import com.lms.lms.Entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    Optional<Enrollment> findByLearnerIdAndCourseId(String learnerId, String courseId);
    List<Enrollment> findByLearnerId(String learnerId);
}