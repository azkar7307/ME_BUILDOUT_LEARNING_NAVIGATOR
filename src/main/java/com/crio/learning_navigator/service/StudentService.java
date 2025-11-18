package com.crio.learning_navigator.service;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;


public interface StudentService {
    StudentResponse createStudent(StudentDTO studentDTO);
}

