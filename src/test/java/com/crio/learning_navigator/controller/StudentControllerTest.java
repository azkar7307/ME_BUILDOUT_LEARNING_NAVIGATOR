package com.crio.learning_navigator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.util.Arrays;
import java.util.List;
import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.exception.GlobalExceptionHandler;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.service.StudentService;
import com.crio.learning_navigator.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private StudentDTO studentDTO;
    private Student sampleStudent;
    private StudentResponse studentResponse;

    @BeforeEach
    void setup() {
        mockMvc = standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        studentDTO = new StudentDTO();
        studentDTO.setName("Student1");
        studentDTO.setEmail("student1@crio.in");

        sampleStudent = modelMapper.map(studentDTO, Student.class);
        sampleStudent.setId(1L);
        studentResponse = modelMapper.map(sampleStudent, StudentResponse.class);
    }


    @Test
    void createStudent_Return_studentResponse() throws Exception {
        // Setup
        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(studentResponse);

        // Execute & Verify
        mockMvc.perform(post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Student1"))
                .andExpect(jsonPath("$.email").value("student1@crio.in"));

        verify(studentService, times(1)).createStudent(any(StudentDTO.class));
    }


    @Test
    void create_Duplicate_Student_Return_409_Conflict() throws Exception {
        // Setup
        when(studentService.createStudent(any(StudentDTO.class)))
                .thenThrow(
                        new ResourceAlreadyExistException(
                                Util.mask(studentDTO.getEmail()),
                                "Student"
                        )
                );

        // Execute & Verify
        mockMvc.perform(post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isConflict())
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.message").value("Student with email '"
                        + Util.mask(studentDTO.getEmail())
                        + "' already exist."))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/student"));

        verify(studentService, times(1)).createStudent(any(StudentDTO.class));
        
    }

    @Test
    void getAllStudent_Return_ListOf_StudentResponse() throws Exception {

        StudentDTO student2 = new StudentDTO();
        student2.setName("Student2");
        student2.setEmail("student2@crio.in");

        Student sampleStudent2 = modelMapper.map(studentDTO, Student.class);
        sampleStudent2.setId(2L);

        List<StudentResponse> students = Arrays.asList(
                modelMapper.map(sampleStudent, StudentResponse.class),
                modelMapper.map(sampleStudent2, StudentResponse.class)
        );

        // Setup
        when(studentService.getAllStudents()).thenReturn(students);

        // Execute & Verify
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

       verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void getStudentById_Existing_Return_StudentResponse() throws Exception {
        
        // Setup
        when(studentService.getStudentById(anyLong())).thenReturn(studentResponse);

        // Execute & Verify
        mockMvc.perform(get("/student/1L"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("student1"));

        verify(studentService, times(1)).getStudentById(anyLong());
    }

    @Test
    void getStudentById_NonExisting_ShouldReturn_404() throws Exception {
        // Setup
        when(studentService.getStudentById(anyLong()))
                .thenThrow(new ResourceNotFoundException("user999", "User"));


        // Execute & Verify
        mockMvc.perform(get("/student/999L"))
                .andExpect(status().isNotFound())
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message").value("Student with ID '999' not found."))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/student/999"));

        verify(studentService, times(1)).getStudentById(anyLong());
    }

    @Test
    void updateStudentById_Return_200() throws Exception {

        // Setup
        when(studentService.updateStudentById(anyLong(), any(StudentDTO.class)))
                .thenReturn(anyString());

        // Execute
        mockMvc.perform(post("/student/1L")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk());

        // Varify
        verify(studentService, times(1)).updateStudentById(
                anyLong(),
                any(StudentDTO.class)
        );
    }

    @Test
    void deleteStudentById_Return_200() throws Exception {

        // Setup
        when(studentService.deleteById(anyLong()))
                .thenReturn(anyString());

        // Execute
        mockMvc.perform(post("/student/1L")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk());

        // Varify
        verify(studentService, times(1)).updateStudentById(
                anyLong(),
                any(StudentDTO.class)
        );
    }

}

