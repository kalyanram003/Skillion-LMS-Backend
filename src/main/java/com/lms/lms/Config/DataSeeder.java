package com.lms.lms.Config;

import com.lms.lms.Entity.*;
import com.lms.lms.Enums.*;
import com.lms.lms.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${lms.seed.always:false}")
    private boolean seedAlways;

    @Override
    public void run(String... args) throws Exception {
        long userCount = userRepository.count();
        long courseCount = courseRepository.count();
        long lessonCount = lessonRepository.count();

        if (seedAlways) {
            // Clear in dependency order, then reseed
            enrollmentRepository.deleteAll();
            lessonRepository.deleteAll();
            courseRepository.deleteAll();
            userRepository.deleteAll();
            seedData();
            return;
        }

        if (userCount == 0 || courseCount == 0 || lessonCount == 0) {
            seedData();
        }
    }

    private void seedData() {
        try {
            // Create Users
            List<User> users = createUsers();
            users = userRepository.saveAll(users);

            // Create Courses
            List<Course> courses = createCourses(users);
            courses = courseRepository.saveAll(courses);

            // Create Lessons
            List<Lesson> lessons = createLessons(courses);
            lessonRepository.saveAll(lessons);

            // Create Enrollments
            List<Enrollment> enrollments = createEnrollments(users, courses);
            enrollmentRepository.saveAll(enrollments);

            System.out.println("Database seeded successfully with sample data!");
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        // Admin Users
        User admin1 = new User();
        admin1.setName("Admin John");
        admin1.setEmail("admin@lms.com");
        admin1.setPassword(passwordEncoder.encode("admin123"));
        admin1.setRole(UserRole.ADMIN);
        users.add(admin1);

        // Creator Users
        User creator1 = new User();
        creator1.setName("Sarah Johnson");
        creator1.setEmail("sarah.johnson@email.com");
        creator1.setPassword(passwordEncoder.encode("password123"));
        creator1.setRole(UserRole.CREATOR);
        creator1.setCreatorBio("Experienced software developer with 10+ years in web development. Passionate about teaching programming concepts.");
        creator1.setCreatorPortfolioUrl("https://sarahjohnson.dev");
        creator1.setCreatorApplicationStatus(ApplicationStatus.APPROVED);
        users.add(creator1);

        User creator2 = new User();
        creator2.setName("Mike Chen");
        creator2.setEmail("mike.chen@email.com");
        creator2.setPassword(passwordEncoder.encode("password123"));
        creator2.setRole(UserRole.CREATOR);
        creator2.setCreatorBio("Data science expert and machine learning researcher. PhD in Computer Science from MIT.");
        creator2.setCreatorPortfolioUrl("https://mikechen.ai");
        creator2.setCreatorApplicationStatus(ApplicationStatus.APPROVED);
        users.add(creator2);

        User creator3 = new User();
        creator3.setName("Emily Rodriguez");
        creator3.setEmail("emily.rodriguez@email.com");
        creator3.setPassword(passwordEncoder.encode("password123"));
        creator3.setRole(UserRole.CREATOR);
        creator3.setCreatorBio("UX/UI designer with expertise in mobile app design. Former Apple designer.");
        creator3.setCreatorPortfolioUrl("https://emilyrodriguez.design");
        creator3.setCreatorApplicationStatus(ApplicationStatus.PENDING);
        users.add(creator3);

        // Learner Users
        for (int i = 1; i <= 11; i++) {
            User learner = new User();
            learner.setName("Learner " + i);
            learner.setEmail("learner" + i + "@email.com");
            learner.setPassword(passwordEncoder.encode("password123"));
            learner.setRole(UserRole.LEARNER);
            users.add(learner);
        }

        return users;
    }

    private List<Course> createCourses(List<User> users) {
        List<Course> courses = new ArrayList<>();
        List<User> creators = users.stream()
                .filter(u -> u.getRole() == UserRole.CREATOR)
                .toList();

        // Course 1: Web Development Fundamentals
        Course course1 = new Course();
        course1.setTitle("Web Development Fundamentals");
        course1.setDescription("Learn HTML, CSS, and JavaScript from scratch. Build responsive websites and understand modern web development practices.");
        course1.setCreator(creators.get(0)); // Sarah Johnson
        course1.setStatus(CourseStatus.PUBLISHED);
        course1.setCreatedAt(LocalDateTime.now().minusDays(30));
        courses.add(course1);

        // Course 2: Python for Data Science
        Course course2 = new Course();
        course2.setTitle("Python for Data Science");
        course2.setDescription("Master Python programming for data analysis, visualization, and machine learning. Includes hands-on projects with real datasets.");
        course2.setCreator(creators.get(1)); // Mike Chen
        course2.setStatus(CourseStatus.PUBLISHED);
        course2.setCreatedAt(LocalDateTime.now().minusDays(25));
        courses.add(course2);

        // Course 3: Mobile App Design Principles
        Course course3 = new Course();
        course3.setTitle("Mobile App Design Principles");
        course3.setDescription("Learn essential UX/UI principles for mobile applications. Design user-friendly interfaces and create engaging user experiences.");
        course3.setCreator(creators.get(2)); // Emily Rodriguez
        course3.setStatus(CourseStatus.DRAFT);
        course3.setCreatedAt(LocalDateTime.now().minusDays(10));
        courses.add(course3);

        // Course 4: Advanced React Development
        Course course4 = new Course();
        course4.setTitle("Advanced React Development");
        course4.setDescription("Deep dive into React hooks, context, performance optimization, and advanced patterns. Build complex applications with confidence.");
        course4.setCreator(creators.get(0)); // Sarah Johnson
        course4.setStatus(CourseStatus.PUBLISHED);
        course4.setCreatedAt(LocalDateTime.now().minusDays(15));
        courses.add(course4);

        // Course 5: Machine Learning with TensorFlow
        Course course5 = new Course();
        course5.setTitle("Machine Learning with TensorFlow");
        course5.setDescription("Build and deploy machine learning models using TensorFlow. From basic concepts to advanced neural networks.");
        course5.setCreator(creators.get(1)); // Mike Chen
        course5.setStatus(CourseStatus.PUBLISHED);
        course5.setCreatedAt(LocalDateTime.now().minusDays(20));
        courses.add(course5);

        return courses;
    }

    private List<Lesson> createLessons(List<Course> courses) {
        List<Lesson> lessons = new ArrayList<>();

        // Lessons for Web Development Fundamentals
        Course webDev = courses.get(0);
        lessons.add(createLesson(webDev, "Introduction to HTML", "https://example.com/lessons/html-intro", 1, "Welcome to HTML basics. Learn about tags, elements, and document structure."));
        lessons.add(createLesson(webDev, "CSS Styling", "https://example.com/lessons/css-styling", 2, "Master CSS selectors, properties, and layout techniques."));
        lessons.add(createLesson(webDev, "JavaScript Fundamentals", "https://example.com/lessons/js-fundamentals", 3, "Learn variables, functions, and DOM manipulation in JavaScript."));
        lessons.add(createLesson(webDev, "Responsive Design", "https://example.com/lessons/responsive-design", 4, "Create mobile-friendly websites with CSS Grid and Flexbox."));

        // Lessons for Python for Data Science
        Course pythonData = courses.get(1);
        lessons.add(createLesson(pythonData, "Python Basics", "https://example.com/lessons/python-basics", 1, "Introduction to Python syntax, data types, and control structures."));
        lessons.add(createLesson(pythonData, "NumPy and Pandas", "https://example.com/lessons/numpy-pandas", 2, "Data manipulation and analysis with NumPy and Pandas libraries."));
        lessons.add(createLesson(pythonData, "Data Visualization", "https://example.com/lessons/data-viz", 3, "Create compelling visualizations with Matplotlib and Seaborn."));
        lessons.add(createLesson(pythonData, "Machine Learning Basics", "https://example.com/lessons/ml-basics", 4, "Introduction to machine learning algorithms and scikit-learn."));

        // Lessons for Mobile App Design Principles
        Course mobileDesign = courses.get(2);
        lessons.add(createLesson(mobileDesign, "Design Thinking Process", "https://example.com/lessons/design-thinking", 1, "Understand the design thinking methodology for mobile apps."));
        lessons.add(createLesson(mobileDesign, "User Interface Design", "https://example.com/lessons/ui-design", 2, "Learn principles of effective mobile UI design."));

        // Lessons for Advanced React Development
        Course reactAdvanced = courses.get(3);
        lessons.add(createLesson(reactAdvanced, "React Hooks Deep Dive", "https://example.com/lessons/react-hooks", 1, "Master useState, useEffect, and custom hooks."));
        lessons.add(createLesson(reactAdvanced, "Context and State Management", "https://example.com/lessons/react-context", 2, "Manage global state with React Context and Redux."));
        lessons.add(createLesson(reactAdvanced, "Performance Optimization", "https://example.com/lessons/react-performance", 3, "Optimize React applications for better performance."));

        // Lessons for Machine Learning with TensorFlow
        Course mlTensorFlow = courses.get(4);
        lessons.add(createLesson(mlTensorFlow, "TensorFlow Basics", "https://example.com/lessons/tensorflow-basics", 1, "Introduction to TensorFlow and neural networks."));
        lessons.add(createLesson(mlTensorFlow, "Building Neural Networks", "https://example.com/lessons/neural-networks", 2, "Create and train neural networks for various tasks."));
        lessons.add(createLesson(mlTensorFlow, "Model Deployment", "https://example.com/lessons/model-deployment", 3, "Deploy machine learning models to production."));

        return lessons;
    }

    private Lesson createLesson(Course course, String title, String contentUrl, int orderIndex, String transcript) {
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(title);
        lesson.setContentUrl(contentUrl);
        lesson.setOrderIndex(orderIndex);
        lesson.setTranscript(transcript);
        return lesson;
    }

    private List<Enrollment> createEnrollments(List<User> users, List<Course> courses) {
        List<Enrollment> enrollments = new ArrayList<>();
        List<User> learners = users.stream()
                .filter(u -> u.getRole() == UserRole.LEARNER)
                .toList();
        List<Course> publishedCourses = courses.stream()
                .filter(c -> c.getStatus() == CourseStatus.PUBLISHED)
                .toList();

        // Create various enrollments with different statuses
        Random random = new Random();

        // Some learners enrolled in multiple courses
        for (int i = 0; i < learners.size() && i < 5; i++) {
            User learner = learners.get(i);
            
            // Enroll in Web Development course
            if (publishedCourses.size() > 0) {
                Enrollment enrollment1 = createEnrollment(learner, publishedCourses.get(0), EnrollmentStatus.COMPLETED);
                enrollment1.setCompletedLessonIds(Set.of("lesson1", "lesson2", "lesson3")); // Some lessons completed
                enrollment1.setCertificateSerialHash("CERT-" + UUID.randomUUID().toString().substring(0, 8));
                enrollment1.setCertificateIssuedAt(LocalDateTime.now().minusDays(5));
                enrollments.add(enrollment1);
            }

            // Enroll in Python course
            if (publishedCourses.size() > 1) {
                Enrollment enrollment2 = createEnrollment(learner, publishedCourses.get(1), EnrollmentStatus.ENROLLED);
                enrollment2.setCompletedLessonIds(Set.of("lesson1")); // Only first lesson completed
                enrollments.add(enrollment2);
            }
        }

        // Additional enrollments for other learners
        for (int i = 5; i < Math.min(learners.size(), 8); i++) {
            User learner = learners.get(i);
            Course randomCourse = publishedCourses.get(random.nextInt(publishedCourses.size()));
            Enrollment enrollment = createEnrollment(learner, randomCourse, EnrollmentStatus.ENROLLED);
            enrollments.add(enrollment);
        }

        // One learner completes a course with certificate
        if (learners.size() > 8 && publishedCourses.size() > 2) {
            User learner = learners.get(8);
            Enrollment enrollment = createEnrollment(learner, publishedCourses.get(2), EnrollmentStatus.COMPLETED);
            enrollment.setCompletedLessonIds(Set.of("lesson1", "lesson2", "lesson3", "lesson4")); // All lessons completed
            enrollment.setCertificateSerialHash("CERT-" + UUID.randomUUID().toString().substring(0, 8));
            enrollment.setCertificateIssuedAt(LocalDateTime.now().minusDays(2));
            enrollments.add(enrollment);
        }

        return enrollments;
    }

    private Enrollment createEnrollment(User learner, Course course, EnrollmentStatus status) {
        Enrollment enrollment = new Enrollment();
        enrollment.setLearner(learner);
        enrollment.setCourse(course);
        enrollment.setStatus(status);
        return enrollment;
    }
}
