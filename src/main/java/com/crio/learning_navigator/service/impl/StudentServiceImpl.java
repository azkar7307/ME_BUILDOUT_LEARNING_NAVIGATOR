package com.crio.learning_navigator.service.impl;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
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
                "Student with email"
            );
        }
        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully | studentId={} | emailMasked={}",
                savedStudent.getId(), Util.mask(savedStudent.getEmail()));
        return modelMapper.map(savedStudent, StudentResponse.class);
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        log.info("Fetched all students from db");
        return students.stream()
                .map(student -> modelMapper.map(student, StudentResponse.class))
                .toList();
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Student")
        );
        log.info("Fetched students from db with id: {}", id);
        return modelMapper.map(student, StudentResponse.class);
    }

    @Override
    public String updateStudentById(Long id, StudentDTO studentDTO) {
        Student studentToUpdate = studentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(id, "Cannot be updated because the Student")
        );
        log.info("Fetched students from db for update with id: {}", id);
        Student student = studentRepository.findByEmail(studentDTO.getEmail());

        if (student != null && !student.getEmail().equals(studentToUpdate.getEmail())) {
            throw new ResourceAlreadyExistException(Util.mask(student.getEmail()), "Other Student with email");
        }

        studentToUpdate.setName(studentDTO.getName());
        studentToUpdate.setEmail(studentDTO.getEmail());
        studentRepository.save(studentToUpdate);
        return "Student with id '" + id + "' updated successfully.";
    }

    @Override
    public String deleteById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(id, "Cannot be deleted because Student")
        );
        log.info("Student for deleting Fetched from db with id: {}", id);
        studentRepository.delete(student);
        return "Student with id '" + id + "' deleted successfully.";
    }
}

