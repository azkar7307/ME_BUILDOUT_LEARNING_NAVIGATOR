package com.crio.learning_navigator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.learning_navigator.dto.response.ExamResponse;
import com.crio.learning_navigator.entity.Exam;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.repository.ExamRepository;
import com.crio.learning_navigator.service.impl.ExamServiceImpl;
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ExamServiceImplTest {
    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private ExamServiceImpl examServiceImpl;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    private Subject subject;
    private Exam sampleTest;


    @BeforeEach
    void setup() {
        subject = new Subject();
        subject.setId(1L);
        subject.setName("Java 301");

        sampleTest = new Exam();
        sampleTest.setId(1L);
        sampleTest.setSubject(subject);
    }

    @Test
    void registerExam_Return_ExamResponse() {
        // Setup
        when(examRepository.findExamBySubjecName(anyString())).thenReturn(null);
        when(examRepository.save(any(Exam.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        ExamResponse examResponse = examServiceImpl.registerExam(1L);

        // Varify
        assertEquals(subject.getName() + " Exam", examResponse.getExamName());
        verify(examRepository, times(1)).findExamBySubjecName(anyString());
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    void registerExam_Again_Throw_ResourceAlreadyExistException() {

        // Setup
        when(examRepository.findExamBySubjecName(anyString())).thenReturn(sampleTest);

        // Execute
        assertThrows(
                ResourceAlreadyExistException.class,
                () -> examServiceImpl.registerExam(1L)
        );

        // Verify
        verify(examRepository, times(1)).findExamBySubjecName(anyString());
        verify(examRepository, never()).save(any(Exam.class));
        verify(examServiceImpl, times(1)).registerExam(anyLong());
    }

    @Test
    void GetAllExam_Return_ListOfExamResponse() {

        Subject subject2 = new Subject();
        subject2.setId(2L);
        subject2.setName("Java 302");

        Exam sampleTest2 = new Exam();
        sampleTest2.setId(2L);
        sampleTest2.setSubject(subject);

        List<Exam> exams = Arrays.asList(sampleTest, sampleTest2);

        // Setup
        when(examRepository.findAll())
                .thenReturn(exams);

        // Execute
        List<ExamResponse> examResponse = examServiceImpl.getAllExams();
        assertEquals(2, examResponse.size());

        // Varify
        verify(examRepository, times(1)).findAll();
        verify(examServiceImpl, times(1)).getAllExams();

    }

    @Test
    void GetExamById_Existing_Return_ExamResponse() {
        // Setup
        when(examRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleTest));

        // Execute
        ExamResponse examResponse = examServiceImpl.getExamById(1L);
        assertEquals(sampleTest.getId(), examResponse.getId());

        // Varify
        verify(examRepository, times(1)).findById(anyLong());
    }

    @Test
    void getExamById_NonExisting_Throw_ResourceNotFoundException() {
        when(examRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Execute
        assertThrows(
                ResourceNotFoundException.class,
                () -> examServiceImpl.getExamById(1L)
        );

        // Varify
        verify(examRepository, times(1)).findById(anyLong());
        verify(examServiceImpl, times(1)).getExamById(anyLong());
    }


    @Test
    void deleteExam_Return_String_success() {
        // Setup
        when(examRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleTest));

        doNothing().when(examRepository).delete(any(Exam.class));

        // Execute
        String response = examServiceImpl.deregisterExam(sampleTest.getId());

        String expectedResponse = "Exam with id '" + sampleTest.getId() + "' deleted successfully.";
        assertEquals(expectedResponse, response);

        // Varify
        verify(examRepository, times(1)).findById(anyLong());
        verify(examRepository, times(1)).delete(any(Exam.class));
    }

}