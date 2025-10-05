# MicroCourses LMS Backend

A Spring Boot-based Learning Management System that supports creators, admins, and learners with course creation, enrollment, progress tracking, and certificate generation.

## Features

- **User Management**: Registration, login, and role-based access (Learner, Creator, Admin)
- **Course Management**: Create, publish, and manage courses with lessons
- **Enrollment System**: Enroll in courses and track progress
- **Auto-Transcripts**: Automatic transcript generation for lesson content
- **Certificate Generation**: Issue certificates with unique serial hashes
- **Rate Limiting**: 60 requests per minute per user
- **Idempotency**: All POST operations support idempotency keys
- **CORS**: Open CORS for frontend integration
- **Pagination**: Support for paginated list endpoints

## API Summary

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Courses
- `GET /api/courses` - List published courses (paginated)
- `GET /api/courses/{id}` - Get course details
- `POST /api/courses` - Create course (Creator only, requires idempotency key)

### Lessons
- `GET /api/lessons/course/{courseId}` - Get lessons for a course
- `GET /api/lessons/{id}` - Get lesson details
- `POST /api/lessons` - Create lesson (Creator only, requires idempotency key)

### Enrollments
- `POST /api/enrollments` - Enroll in course (requires idempotency key)
- `POST /api/enrollments/{id}/complete-lesson` - Mark lesson as completed
- `GET /api/enrollments/progress` - Get user progress

### Admin
- `GET /api/admin/review/courses` - Get courses pending review
- `PATCH /api/admin/review/courses/{id}/approve` - Approve course for publishing
- `PATCH /api/admin/review/creators/{id}/approve` - Approve creator application

## Example Requests and Responses

### User Registration
```bash
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "id": "uuid-here",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "LEARNER",
  "creatorApplicationStatus": "PENDING"
}
```

### Course Creation (with Idempotency)
```bash
POST /api/courses
Content-Type: application/json
X-User-Id: creator-uuid-here
Idempotency-Key: unique-key-123

{
  "title": "Introduction to Spring Boot",
  "description": "Learn Spring Boot fundamentals"
}
```

**Response:**
```json
{
  "id": "course-uuid-here",
  "title": "Introduction to Spring Boot",
  "description": "Learn Spring Boot fundamentals",
  "status": "DRAFT",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Course Listing (Paginated)
```bash
GET /api/courses?limit=10&offset=0
```

**Response:**
```json
{
  "items": [
    {
      "id": "course-uuid-1",
      "title": "Course 1",
      "description": "Description 1",
      "status": "PUBLISHED"
    }
  ],
  "next_offset": 10
}
```

### Enrollment
```bash
POST /api/enrollments
Content-Type: application/json
X-User-Id: learner-uuid-here
Idempotency-Key: enrollment-key-456

{
  "courseId": "course-uuid-here"
}
```

**Response:**
```json
{
  "id": "enrollment-uuid-here",
  "status": "ENROLLED",
  "completedLessonIds": []
}
```

## Test User Credentials

### Admin User
- **Email**: admin@lms.com
- **Password**: admin123
- **Role**: ADMIN

### Creator User
- **Email**: creator@lms.com
- **Password**: creator123
- **Role**: CREATOR (after approval)

### Learner User
- **Email**: learner@lms.com
- **Password**: learner123
- **Role**: LEARNER

## Seed Data

The application starts with a PostgreSQL database. You can access the database through the application APIs at `http://localhost:8090/api` with:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### Sample Data Structure
1. **Users**: Admin, Creator, and Learner accounts
2. **Courses**: Sample courses in DRAFT and PUBLISHED status
3. **Lessons**: Sample lessons with auto-generated transcripts
4. **Enrollments**: Sample enrollments with progress tracking

## Error Format

All errors follow a uniform format:
```json
{
  "error": {
    "code": "ERROR_CODE",
    "field": "field_name",
    "message": "Human readable error message"
  }
}
```

### Common Error Codes
- `FIELD_REQUIRED` - Required field is missing
- `NOT_FOUND` - Resource not found
- `BAD_REQUEST` - Invalid request data
- `VALIDATION_ERROR` - Validation failed
- `RATE_LIMIT` - Rate limit exceeded (429 status)
- `UNPUBLISHED` - Course not available to learners

## Rate Limiting

- **Limit**: 60 requests per minute per user
- **Identification**: Uses `X-User-Id` header, falls back to IP address
- **Response**: 429 status with `{"error": {"code": "RATE_LIMIT"}}`

## Idempotency

All POST operations that create resources require an `Idempotency-Key` header:
- If key exists: Returns existing resource
- If key is new: Creates new resource and stores mapping
- Missing key: Returns 400 error

## CORS Configuration

CORS is configured to allow all origins, methods, and headers for frontend integration:
- **Origins**: `*`
- **Methods**: `*`
- **Headers**: `*`
- **Exposed Headers**: `X-Idempotent-Resource`

## Running the Application

1. **Prerequisites**:
   - Java 21+
   - Maven 3.6+

2. **Build and Run**:
   ```bash
   cd Backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **Access Points**:
   - **API Base URL**: `http://localhost:8090/api`
   - **Database**: PostgreSQL (Neon)
   - **Health Check**: `http://localhost:8090/actuator/health` (if actuator is added)

## Database Configuration

### Development (H2)
- **Type**: In-memory H2 database
- **DDL**: `create-drop` (recreates schema on restart)
- **Console**: Enabled at `/h2-console`

### Production (PostgreSQL)
Uncomment PostgreSQL configuration in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
spring.datasource.username=<USERNAME>
spring.datasource.password=<PASSWORD>
spring.jpa.hibernate.ddl-auto=update
```

## Architecture

### Entities
- **User**: Users with roles (LEARNER, CREATOR, ADMIN)
- **Course**: Courses with status (DRAFT, PUBLISHED, REJECTED)
- **Lesson**: Lessons with unique order within courses
- **Enrollment**: User enrollments with progress tracking

### Services
- **UserService**: User management and authentication
- **CourseService**: Course CRUD operations
- **LessonService**: Lesson management and transcript generation
- **EnrollmentService**: Enrollment and progress tracking
- **IdempotencyService**: Idempotency key management

### Configuration
- **SecurityConfig**: Spring Security configuration
- **CorsConfig**: CORS configuration
- **RateLimitFilter**: Rate limiting implementation
- **IdempotencyInterceptor**: Idempotency enforcement
- **ExceptionHandlerAdvice**: Global exception handling

## Testing

The application includes comprehensive error handling and validation. Test the following scenarios:

1. **Authentication Flow**:
   - Register new user
   - Login with credentials
   - Access protected endpoints

2. **Creator Flow**:
   - Apply for creator status
   - Admin approves application
   - Create courses and lessons

3. **Learner Flow**:
   - Browse published courses
   - Enroll in courses
   - Complete lessons
   - Receive certificates

4. **Admin Flow**:
   - Review creator applications
   - Approve/reject courses
   - Monitor system

## Future Enhancements

- JWT-based authentication
- File upload for course content
- Real-time progress notifications
- Advanced analytics and reporting
- Integration with external transcription services
- Email notifications for course completion
- Advanced search and filtering
- Course categories and tags
