package com.crio.learning_navigator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import com.crio.learning_navigator.dto.SubjectDTO;
import com.crio.learning_navigator.dto.response.SubjectResponse;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.repository.SubjectRepository;
import com.crio.learning_navigator.service.SubjectService;
import org.modelmapper.ModelMapper;

@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final ModelMapper modelMapper;

    @Override
    public SubjectResponse registerSubject(SubjectDTO subjectDTO) {
        if (subjectRepository.existsByNameIgnoreCase(subjectDTO.getName())) {
            throw new ResourceAlreadyExistException(subjectDTO.getName(), "Subject with name");
        }
        Subject subject = modelMapper.map(subjectDTO, Subject.class);
        Subject savedSubject = subjectRepository.save(subject);
        log.info("Subject with id '{}' created successfully ", savedSubject.getId());
        return modelMapper.map(savedSubject, SubjectResponse.class);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        log.info("Fetched all subjects from db");
        return subjects.stream()
                .map(subject -> modelMapper.map(subject, SubjectResponse.class))
                .toList();
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Subject")
        );
        log.info("Fetched subject from db with id: {}", id);
        return modelMapper.map(subject, SubjectResponse.class);
    }

    @Override
    public String updateSubject(Long id, SubjectDTO subjectToUpdate) {
        if (subjectRepository.existsByNameIgnoreCase(subjectToUpdate.getName())) {
            throw new ResourceAlreadyExistException(subjectToUpdate.getName(), "Subject with name");
        }

        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Subject")
        );
        log.info("Fetched subject from db for update with id: {}", id);
        subject.setName(subjectToUpdate.getName());
        subjectRepository.save(subject);
        return "Subject with id '" + id + "' updated successfully.";
    }

    @Override
    public String deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Cannot be deleted because subject")
        );
        log.info("Subject for deleting Fetched from db with id: {}", id);
        subjectRepository.delete(subject);
        return "Subject with id '" + id + "' deleted successfully.";
    }
    
}
