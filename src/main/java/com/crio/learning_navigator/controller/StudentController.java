package com.crio.learning_navigator.controller;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;


    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
        @Valid @RequestBody StudentDTO studentDTO
    ){
        log.info("Request received for creating a student");
        StudentResponse savedStudent = studentService.createStudent(studentDTO);
        log.info("Student created successfully with id: {}", savedStudent.getId());
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        log.info("Request received for fetching all students");
        List<StudentResponse> studentResponses = studentService.getAllStudents();
        log.info("All students fetched successfully");
        return new ResponseEntity<>(studentResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable("id") Long id) {
        log.info("Request received for fetching student by id: {}", id);
        StudentResponse studentResponse = studentService.getStudentById(id);
        log.info("Student fetched successfully by id {}", id);
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudentById(
        @PathVariable("id") Long id, 
        @Valid @RequestBody StudentDTO studentDTO
    ) {
        log.info("Request received for updating a student by id {}", id);
        String response = studentService.updateStudentById(id, studentDTO);
        log.info("Student updated successfully by id: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        log.info("Request received for deleting a students by id: {}", id);
        String response = studentService.deleteById(id);
        log.info("Student deleted successfully by id {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
