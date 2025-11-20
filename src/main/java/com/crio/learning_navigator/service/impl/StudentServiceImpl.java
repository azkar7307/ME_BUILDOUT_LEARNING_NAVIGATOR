package com.crio.learning_navigator.service.impl;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.repository.StudentRepository;
import com.crio.learning_navigator.service.StudentService;
import com.crio.learning_navigator.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.modelmapper.ModelMapper;

@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Override
    public StudentResponse createStudent(StudentDTO studentDTO) {
        Student student = modelMapper.map(studentDTO, Student.class);
        Student studentFromDb = studentRepository.findByEmail(student.getEmail());
        if (studentFromDb != null) {
            throw new ResourceAlreadyExistException(
                Util.mask(studentFromDb.getEmail()), 
                "Student"
            );
        }
        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully | studentId={} | emailMasked={}",
                savedStudent.getId(), Util.mask(savedStudent.getEmail()));
        return modelMapper.map(savedStudent, StudentResponse.class);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return null;
    }

    @Override
    public StudentResponse getStudentById(long id) {
        return null;
    }

    @Override
    public String updateStudentById(Long id, StudentDTO studentToUpdate) {
        return null;
    }

    @Override
    public String deleteById(long id) {
        return null;
    }
}

