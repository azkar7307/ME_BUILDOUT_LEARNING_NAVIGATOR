package com.crio.learning_navigator.service;

import com.crio.learning_navigator.dto.response.StudentResponse;

public interface StudentEnrollmentService {

    StudentResponse enrollStudentInSubject(Long studentId, Long subjectId);

    StudentResponse enrollStudentInExam(Long studentId, Long examId);

}
