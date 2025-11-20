package com.crio.learning_navigator.service;

import java.util.List;
import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;


public interface StudentService {

    StudentResponse createStudent(StudentDTO studentDTO);
    
    StudentResponse getStudentById(long id);

    String updateStudentById(Long id, StudentDTO studentToUpdate);

    String deleteById(long id);

    List<StudentResponse> getAllStudents();
}

