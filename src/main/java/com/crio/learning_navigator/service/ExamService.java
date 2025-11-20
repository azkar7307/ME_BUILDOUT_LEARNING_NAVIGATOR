package com.crio.learning_navigator.service;

import java.util.List;
import com.crio.learning_navigator.dto.response.ExamResponse;

public interface ExamService {
    
    ExamResponse registerExam(Long subjectId);

    List<ExamResponse> getAllExams();

    ExamResponse getExamById(Long id);

    String deregisterExam(Long examId);
}
