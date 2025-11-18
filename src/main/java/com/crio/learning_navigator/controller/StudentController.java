package com.crio.learning_navigator.controller;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;


    @PostMapping
    public ResponseEntity<StudentResponse> CreateStudent(@Valid @RequestBody StudentDTO studentDTO){
        log.info("Request received for creating a student");
        StudentResponse savedStudent = studentService.createStudent(studentDTO);
        log.info("Student created successfully with id: {}", savedStudent.getId());
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }
}
