package com.crio.learning_navigator.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private List<SubjectDTO> enrolledSubjects;
    private List<ExamDTO> enrolledExams;
}