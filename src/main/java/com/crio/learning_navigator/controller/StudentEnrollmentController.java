package com.crio.learning_navigator.controller;

import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.service.StudentEnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/students")
public class StudentEnrollmentController {

    private final StudentEnrollmentService studentEnrollmentService;

    @PostMapping("/{studentId}/subjects/{subjectId}")
    public ResponseEntity<StudentResponse> enrollInSubject(
        @PathVariable("studentId") Long studentId,
        @PathVariable("subjectId") Long subjectId
    ) {
        log.info(
            "A request received to enroll student with id '{}' in the subject with id '{}'",
            studentId, 
            subjectId
        );
        StudentResponse studentResponse = studentEnrollmentService.enrollStudentInSubject(
            studentId, 
            subjectId
        );
        log.info(
            "Student with id '{}' enrolled successfully in subject with id '{}'",
            studentId,
            subjectId
        );
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }

    @PostMapping("/{studentId}/exams/{examId}")
    public ResponseEntity<StudentResponse> enrollInExam(
        @PathVariable Long studentId, 
        @PathVariable Long examId
    ) {
        log.info(
            "A request received to enroll student with id '{}' in the exam with id '{}'",
            studentId, 
            examId
        );
        StudentResponse studentResponse = studentEnrollmentService.enrollStudentInExam(
            studentId, 
            examId
        );
        log.info(
            "Student with id '{}' enrolled successfully in exam with id '{}'",
            studentId,
            examId
        );
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }
}
