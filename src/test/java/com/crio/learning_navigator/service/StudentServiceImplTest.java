package com.crio.learning_navigator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
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
        sampleStudent.setId(1L);
    }

    @Test
    void createStudent_Return_StudentResponse() {
        // Setup
        when(studentRepository.findByEmail(anyString())).thenReturn(null);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        StudentResponse studentResponse = studentServiceImpl.createStudent(studentDTO);

        assertEquals(studentDTO.getName(), studentResponse.getName());

        // Varify
        verify(studentRepository, times(1)).findByEmail(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createDuplicateStudent_Throw_ResourceAlreadyExistException() {

        // Setup
        when(studentRepository.findByEmail(anyString())).thenReturn(sampleStudent);

        // Execute
        assertThrows(
                ResourceAlreadyExistException.class,
                () -> studentServiceImpl.createStudent(studentDTO)
        );

        // Varify
        verify(studentRepository, times(1)).findByEmail(anyString());
        verify(studentRepository, never()).save(any(Student.class));

    }

    @Test
    void getAllStudent_Return_ListOf_StudentResponse() {

        StudentDTO student2 = new StudentDTO();
        student2.setName("Student2");
        student2.setEmail("student2@crio.in");

        Student sampleStudent2 = modelMapper.map(studentDTO, Student.class);
        sampleStudent2.setId(2L);

        List<Student> students = new ArrayList<>(Arrays.asList(sampleStudent, sampleStudent2));

        // Setup
        when(studentRepository.findAll())
                .thenReturn(students);

        // Execute
        List<StudentResponse> studentResponses = studentServiceImpl.getAllStudents();
        assertEquals(2, studentResponses.size());

        // Varify
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void GetStudentById_Existing_Return_StudentResponse() {
        // Setup
        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleStudent));

        // Execute
        StudentResponse studentResponse = studentServiceImpl.getStudentById(anyLong());
        assertEquals(sampleStudent.getId(), studentResponse.getId());
        assertEquals(sampleStudent.getName(), studentResponse.getName());

        // Varify
        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void getStudentById_NonExisting_Throw_ResourceNotFoundException() {


        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Execute
        assertThrows(
                ResourceNotFoundException.class,
                () -> studentServiceImpl.getStudentById(anyLong())
        );

        // Varify
        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateStudentById_updateName_Return_String_Success() {
        StudentDTO studentToUpdate = new StudentDTO();
        studentToUpdate.setName("John Doe");
        studentToUpdate.setEmail(sampleStudent.getEmail());

        // Setup
        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleStudent));

        when(studentRepository.findByEmail(anyString()))
                .thenReturn(sampleStudent);

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // Execute
        String response = studentServiceImpl.updateStudentById(sampleStudent.getId(), studentToUpdate);

        String expectedResponse = "Student with id '" + sampleStudent.getId() + "' updated successfully.";
        assertEquals(expectedResponse, response);

        // Varify
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findByEmail(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void updateStudentById_UpdateNameAndEmail_Return_String_Success() {
        StudentDTO studentToUpdate = new StudentDTO();
        studentToUpdate.setName("John Doe");
        studentToUpdate.setEmail("johndoe@crio.in");

        // Setup
        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleStudent));

        when(studentRepository.findByEmail(anyString()))
                .thenReturn(null);

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // Execute
        String response = studentServiceImpl.updateStudentById(sampleStudent.getId(), studentToUpdate);

        String expectedResponse = "Student with id '" + sampleStudent.getId() + "' updated successfully.";
        assertEquals(expectedResponse, response);

        // Varify
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findByEmail(anyString());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void updateStudentById_UpdateNameAndEmail_Throw_Exception() {
        StudentDTO student2 = new StudentDTO();
        student2.setName("John Doe");
        student2.setEmail("john@crio.in");

        Student existingStudent = modelMapper.map(student2, Student.class);
        existingStudent.setId(2L);

        // try to update name and email of sampleStudent
        StudentDTO studentToUpdate = new StudentDTO();
        studentToUpdate.setName("John Smith");
        // insert the existing email
        studentToUpdate.setEmail(existingStudent.getEmail());

        // Setup
        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleStudent));

        when(studentRepository.findByEmail(anyString()))
                .thenReturn(existingStudent);


        // Execute
        assertThrows(
                ResourceAlreadyExistException.class,
                () -> studentServiceImpl.updateStudentById(sampleStudent.getId(), studentToUpdate)
        );

        // Varify
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void deleteStudentById_Return_String() {
        // Setup
        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleStudent));

        doNothing().when(studentRepository).delete(any(Student.class));

        // Execute
        String response = studentServiceImpl.deleteById(sampleStudent.getId());

        String expectedResponse = "Student with id '" + sampleStudent.getId() + "' deleted successfully.";
        assertEquals(expectedResponse, response);

        // Varify
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).delete(any(Student.class));

    }

}
