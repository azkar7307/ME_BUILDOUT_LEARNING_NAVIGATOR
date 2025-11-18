package com.crio.learning_navigator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.exception.GlobalExceptionHandler;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
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
    private StudentResponse studentResponse;

    @BeforeEach
    void setup() {
        mockMvc = standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        studentDTO = new StudentDTO();
        studentDTO.setName("Student1");
        studentDTO.setEmail("student1@crio.in");

        Student sampleSavedStudent = modelMapper.map(studentDTO, Student.class);
        sampleSavedStudent.setId(1L);
        studentResponse = modelMapper.map(sampleSavedStudent, StudentResponse.class);
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
    }

}

