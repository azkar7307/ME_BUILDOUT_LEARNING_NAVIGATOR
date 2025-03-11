# Learning-Management-System-LMS

## Overview
The Learning Management System (LMS) is a system that handles exam registrations, subject enrollments, and student details. The API provides functionality to manage students, subjects, and exams with appropriate error handling and constraints.

## API Documentation
https://documenter.getpostman.com/view/32910654/2sAYQanBqv

The API allows CRUD operations on **Students**, **Subjects**, and **Exams**.

## Features
- Complete CRUD operations for **Student**, **Subject**, and **Exam** management.
- Ensures that students can only register for an exam if they are enrolled in the respective subject.
- Ensures that students will not be able to enroll for an exam if they have already been enrolled.
- Ensures that students will not be able to register for a subject if they have already been registered.
- Handles common errors and returns appropriate HTTP codes (e.g., 404 for not found, 409 for conflicts).
- Unit tests with **MockMvc** and **Mockito**.

## Entities
### Student
The **Student** entity includes the following fields:
- **Student ID**: Unique identifier
- **Student Name**: Full name
- **Enrolled Subjects**: List of subjects the student is enrolled in
- **Registered Exams**: List of exams the student has registered for

### Subject
The **Subject** entity includes the following fields:
- **Subject ID**: Unique identifier
- **Subject Name**: Name of the subject
- **Enrolled Students**: List of students enrolled in this subject

### Exam
The **Exam** entity includes the following fields:
- **Exam ID**: Unique identifier
- **Subject**: Subject associated with this exam
- **Enrolled Students**: List of students registered for this exam

## API Endpoints

### 1. Register Student for a Subject
- **URL**: `/students/{studentId}/subjects/{subjectId}`
- **Method**: POST
- **Description**: Allows a student to register for a subject.
- **Exceptions**:
  - 409 **Conflict**: If the student is already enrolled in the subject.
  - 404 **Not Found**: If either the student or the subject is not found.
  - 400 **Bad Request**: If there are invalid request parameters.

### 2. Register Student for an Exam
- **URL**: `/students/{studentId}/exams/{examId}`
- **Method**: POST
- **Description**: Allows a student to register for an exam based on subject enrollment.
- **Exceptions**:
  - 400 **Bad Request**: If the student is not enrolled in the subject associated with the exam.
  - 404 **Not Found**: If either the student or the exam is not found.
  - 409 **Conflict**: If the student is already registered for the exam.

### 3. Get All Students
- **URL**: `/students`
- **Method**: GET
- **Description**: Retrieve all students.
- **Success Response**:
  - 200 **OK**: Returns a list of all students.

### 4. Get a Specific Student
- **URL**: `/students/{studentId}`
- **Method**: GET
- **Description**: Retrieve a specific student by their ID.
- **Exceptions**:
  - 404 **Not Found**: If the student is not found.

## Error Handling
### Common Error Responses
- **404 - Not Found**: Resource could not be found (e.g., student or subject not found).
- **400 - Bad Request**: Invalid input, such as non-existent subject or student.
- **409 - Conflict**: When attempting to re-register a student in an already enrolled subject.
- **500 - Internal Server Error**: Server encountered an unexpected condition.

### Exception Class Examples
- **StudentAlreadyEnrolledForExamException**: Thrown when a student tries to register for an exam which the student has already been registered to.
    - **HTTP Status Code**: 409 Conflict
  
- **StudentAlreadyEnrolledInSubjectException**: Thrown when a student tries to enroll in a subject they're already enrolled in.
    - **HTTP Status Code**: 409 Conflict
      
- **StudentNotEnrolledForSubjectException**: Thrown when a student tries to register for an exam without being enrolled in the corresponding subject.
    - **HTTP Status Code**: 400 Bad Request
      
- **ResourceNotFoundException**: Thrown when any resource is not found in the server.
    - **HTTP Status Code**: 404 Not Found

### Global Exception Handling
All exceptions are handled globally using **@RestControllerAdvice**, which ensures that proper error responses are sent for invalid operations.

## Unit Tests
This application includes unit tests using **MockMvc** and **Mockito** to verify:
- Correct handling of CRUD operations
- Registration validation
- Exception handling

## Getting Started
### Prerequisites
- Java 11+
- Spring Boot
- MySQL Database

### Running the Project
1. Clone the repository
2. Configure MySQL in `application.properties`
3. Run the application using:

```bash
mvn spring-boot:run
```

Access the API at `http://localhost:8080`.

