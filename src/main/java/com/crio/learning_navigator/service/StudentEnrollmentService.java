package com.crio.learning_navigator.service;

import com.crio.learning_navigator.dto.response.StudentResponse;

public interface StudentEnrollmentService {

    StudentResponse enrollInSubject(Long studentId, Long subjectId);

    StudentResponse enrollInExam(Long studentId, Long examId);

}
