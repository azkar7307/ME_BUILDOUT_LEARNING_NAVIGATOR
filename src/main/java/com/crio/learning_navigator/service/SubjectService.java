package com.crio.learning_navigator.service;

import java.util.List;
import com.crio.learning_navigator.dto.SubjectDTO;
import com.crio.learning_navigator.dto.response.SubjectResponse;

public interface SubjectService {
    
    SubjectResponse registerSubject(SubjectDTO subjectDTO);

    List<SubjectResponse> getAllSubjects();

    SubjectResponse getSubjectById(Long id);

    String updateSubject(Long id, SubjectDTO subjectToUpdate);

    public String deleteSubject(Long id);

}
