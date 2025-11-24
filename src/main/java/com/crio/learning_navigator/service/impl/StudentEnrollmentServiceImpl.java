package com.crio.learning_navigator.service.impl;

import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.service.StudentEnrollmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.crio.learning_navigator.entity.Exam;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.exception.StudentNotEnrolledInSubjectException;
import com.crio.learning_navigator.repository.ExamRepository;
import com.crio.learning_navigator.repository.StudentRepository;
import com.crio.learning_navigator.repository.SubjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public StudentResponse enrollStudentInSubject(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ResourceNotFoundException(
                studentId,
                "Student cannot enroll in exam/subject because the because the student")
        );
        log.info("Fetched a student with Id '{}' from db for enrolling in a subject", studentId);
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(
            () -> new ResourceNotFoundException(subjectId, "Subject")
        );
        log.info("Fetched subject to be enrolled from db with id '{}' ", subjectId);
        
        if(isStudentEnrolledInSubject(student, subject)) {
            throw new ResourceAlreadyExistException(
                "Student with id: " 
                + studentId 
                + " has already enrolled in subject" 
            );
        }
        student.getSubjects().add(subject); // for database update 
        subject.getStudents().add(student); // for maintaining bidirectional consistensitency
        // estalablish the relationship between student and subject
        Student savedStudent = studentRepository.save(student);
        log.info(
            "Student with id '{}' enrolled in the subject with id '{}' successfully", 
            studentId,
            subjectId
        );
        return modelMapper.map(savedStudent, StudentResponse.class);
    }

    @Override
    public StudentResponse enrollStudentInExam(Long studentId, Long examId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
            () -> new ResourceNotFoundException(
                studentId, 
                "Student cannot enroll in exam/subject because the Student")
        );
        log.info("Fetched a student with Id '{}' from db for enrolling in a exam", studentId);
        Exam exam = examRepository.findById(examId).orElseThrow(
            () -> new ResourceNotFoundException(examId, "Exam")
        );

        if (student.getExams().contains(exam)) {
            throw new ResourceAlreadyExistException(
                "Student with id: "         
                + studentId 
                + " has already enrolled for this particular exam with id: " 
                + examId 
            );
        }

        if (!isStudentEnrolledInSubject(student, exam.getSubject())) {
            throw new StudentNotEnrolledInSubjectException(
                "Student with id '" 
                + studentId 
                + "' not enrolled in a subject with id '" 
                + exam.getSubject().getId() + 
                "'"
            );        
        }

        student.getExams().add(exam);
        exam.getStudents().add(student); 
        Student savedStudent = studentRepository.save(student);
        log.info(
            "Student with id '{}' enrolled in the exam with id '{}' successfully", 
            studentId,
            examId
        );
        return modelMapper.map(savedStudent, StudentResponse.class);
    }

    private boolean isStudentEnrolledInSubject(Student student, Subject subject) {
        return student.getSubjects().contains(subject);
    }
}
