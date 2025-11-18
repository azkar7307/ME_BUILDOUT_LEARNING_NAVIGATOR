package com.crio.learning_navigator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.repository.StudentRepository;
import com.crio.learning_navigator.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class StudentServiceImplTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentServiceImpl;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    private StudentDTO studentDTO;
    private Student sampleStudent;

    @BeforeEach
    void setup() {
        studentDTO = new StudentDTO();
        studentDTO.setName("Student1");
        studentDTO.setEmail("student1@crio.in");

        sampleStudent = modelMapper.map(studentDTO, Student.class);
    }

    @Test
    void createStudent_Return_StudentResponse() {
        // Setup
        when(studentRepository.findByEmail(anyString())).thenReturn(null);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute and varify
        StudentResponse studentResponse = studentServiceImpl.createStudent(studentDTO);

        assertEquals(studentDTO.getName(), studentResponse.getName());
        verify(studentRepository, times(1)).findByEmail(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createDuplicateStudent_Throw_ResourceAlreadyExistException() {

        // Setup
        when(studentRepository.findByEmail(anyString())).thenReturn(sampleStudent);

        // Execute & Verify
        assertThrows(
                ResourceAlreadyExistException.class,
                () -> studentServiceImpl.createStudent(studentDTO)
        );

        verify(studentRepository, times(1)).findByEmail(anyString());
        verify(studentRepository, never()).save(any(Student.class));

    }

}
