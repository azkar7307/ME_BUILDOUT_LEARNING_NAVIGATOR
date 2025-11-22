package com.crio.learning_navigator.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import com.crio.learning_navigator.dto.response.ExamResponse;
import com.crio.learning_navigator.entity.Exam;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.repository.ExamRepository;
import com.crio.learning_navigator.repository.SubjectRepository;
import com.crio.learning_navigator.service.ExamService;
import org.modelmapper.ModelMapper;

@Slf4j
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ExamResponse registerExam(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new ResourceNotFoundException(subjectId, "Subject with name")
        );
        log.info("Fetched subject from db with id: {}", subjectId);
        if (subject.getExam() != null) {
            throw new ResourceAlreadyExistException("Exam already registered with subjectId '" + subjectId + "'");
        }
        Exam exam = new Exam();
        exam.setSubject(subject);
        subject.setExam(exam); // For maintaining bidirectional relationship
        Subject examSubject = subjectRepository.save(subject); // Saving subject also saves exam due to Casscade.ALL
        log.info("Exam with id '{}' registered successfully ", examSubject.getExam().getId());
        return modelMapper.map(examSubject.getExam(), ExamResponse.class);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        log.info("Fetched all exams from db");
        return exams.stream()
                .map(exam -> modelMapper.map(exam, ExamResponse.class))
                .toList();
    }

    @Override
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Subject")
        );
        log.info("Fetched exam from db with id: {}", id);
        ExamResponse examResponse =  modelMapper.map(exam, ExamResponse.class);
        return examResponse;
    }

    @Override
    public String deregisterExam(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Cannot be deleted because exam")
        );
        log.info("Exam for deleting, Fetched from db with id: {}", id);
        examRepository.delete(exam);
        return "Exam with id '" + id + "' deleted successfully";
    }
    
}
