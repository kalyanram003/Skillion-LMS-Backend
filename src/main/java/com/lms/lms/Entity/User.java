package com.lms.lms.Entity;

import com.lms.lms.Enums.ApplicationStatus;
import com.lms.lms.Enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role; // LEARNER, CREATOR, ADMIN

    @Column(columnDefinition = "TEXT")
    private String creatorBio; // null if not creator

    @Column
    private String creatorPortfolioUrl;

    @Column
    @Enumerated(EnumType.STRING)
    private ApplicationStatus creatorApplicationStatus; // PENDING, APPROVED, REJECTED

    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private Set<Course> courses;

    @OneToMany(mappedBy = "learner")
    @JsonIgnore
    private Set<Enrollment> enrollments;
}
