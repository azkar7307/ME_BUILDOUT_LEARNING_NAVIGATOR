package com.crio.learning_navigator.controller;

import com.crio.learning_navigator.dto.response.ExamResponse;
import com.crio.learning_navigator.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;

    @PostMapping("/subjects/{subjectId}")
    public ResponseEntity<ExamResponse> registerExam(@PathVariable Long subjectId) {
        log.info("Request received for register exam for subject {}", subjectId);
        ExamResponse savedExam = examService.registerExam(subjectId);
        log.info("Exam registered successfully with id: {}", savedExam.getId());
        return new ResponseEntity<>(savedExam, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExamResponse>> getAllExams() {
        log.info("Request received for fetching all exam");
        List<ExamResponse> examResponses = examService.getAllExams();
        log.info("All exams fetched successfully");
        return new ResponseEntity<>(examResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id){
        log.info("Request received for fetching exam by id: {}", id);
        ExamResponse examResponse = examService.getExamById(id);
        log.info("Exam fetched successfully by id {}", id);
        return new ResponseEntity<>(examResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deregisterExam(@PathVariable Long id) {
        log.info("Request received for deleting exam by id: {}", id);
        String response = examService.deregisterExam(id);
        log.info("Exam deleted successfully by id {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
