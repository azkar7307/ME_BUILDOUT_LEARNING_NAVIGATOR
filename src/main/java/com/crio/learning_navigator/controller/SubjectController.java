package com.crio.learning_navigator.controller;

import com.crio.learning_navigator.dto.SubjectDTO;
import com.crio.learning_navigator.dto.response.SubjectResponse;
import com.crio.learning_navigator.service.SubjectService;
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
@RequestMapping("/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<SubjectResponse> registerSubject(@Valid @RequestBody SubjectDTO subjectDTO){
        log.info("Request received for registering a subject");
        SubjectResponse savedSubject = subjectService.registerSubject(subjectDTO);
        log.info("Subject registered successfully with id: {}", savedSubject.getId());
        return new ResponseEntity<>(savedSubject, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects() {
        log.info("Request received for fetching all subjects");
        List<SubjectResponse> subjectResponses = subjectService.getAllSubjects();
        log.info("All subjects fetched successfully");
        return new ResponseEntity<>(subjectResponses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long id){
        log.info("Request received for fetching subject by id: {}", id);
        SubjectResponse subjectResponse = subjectService.getSubjectById(id);
        log.info("Subject fetched successfully by id {}", id);
        return new ResponseEntity<>(subjectResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDTO subjectDTO) {
        log.info("Request received for updating a subject with id {}", id);
        String response = subjectService.updateSubject(id, subjectDTO);
        log.info("Subject updated successfully by id: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        log.info("Request received for deleting a subject with id: {}", id);
        String response = subjectService.deleteSubject(id);
        log.info("Subject deleted successfully by id {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
