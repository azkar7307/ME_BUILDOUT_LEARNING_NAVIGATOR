package com.crio.learning_navigator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.crio.learning_navigator.config.AppConfig;
import com.crio.learning_navigator.dto.response.ExamResponse;
import com.crio.learning_navigator.entity.Exam;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.repository.ExamRepository;
import com.crio.learning_navigator.repository.SubjectRepository;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ExamServiceImplTest {
    
    @Mock
    private ExamRepository examRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Spy
    private ModelMapper modelMapper = new AppConfig().modelMapper();

    @InjectMocks
    private ExamServiceImpl examServiceImpl;

    private Subject sampleSubject;
    private Exam sampleExam;


    @BeforeEach
    void setup() {
        sampleSubject = new Subject();
        sampleSubject.setId(1L);
        sampleSubject.setName("Java 301");

        sampleExam = new Exam();
        sampleExam.setId(1L);
        sampleExam.setSubject(sampleSubject);
    }

    @Test
    void registerExam_Return_ExamResponse() {

        Subject subject2 = new Subject();
        subject2.setId(2L);
        subject2.setName("Java 302");

        Exam exam = new Exam();
        exam.setId(1L);
        
        // register exam
        exam.setSubject(subject2);
        subject2.setExam(exam);

        // Setup
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(sampleSubject));
        when(subjectRepository.save(any(Subject.class)))
            .thenReturn(subject2);

        // Execute
        ExamResponse examResponse = examServiceImpl.registerExam(1L);
        assertEquals(subject2.getName() + " Exam", examResponse.getExamName());

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(subjectRepository, times(1)).save(any(Subject.class));
        verify(modelMapper, times(1)).map(any(Exam.class), eq(ExamResponse.class));

    }

    @Test
    void registerExam_Again_Throw_ResourceAlreadyExistException() {
        
        // mocking the exam is registered already
        sampleExam.setSubject(sampleSubject);
        sampleSubject.setExam(sampleExam);

        // Setup
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(sampleSubject));

        // Execute
        assertThrows(
            ResourceAlreadyExistException.class,
            () -> examServiceImpl.registerExam(1L)
        );

        // Verify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(subjectRepository, never()).save(any(Subject.class));
        verify(modelMapper, never()).map(any(Exam.class), eq(ExamResponse.class));

    }

    @Test
    void GetAllExam_Return_ListOfExamResponse() {

        Subject subject2 = new Subject();
        subject2.setId(2L);
        subject2.setName("Java 302");

        Exam sampleExam2 = new Exam();
        sampleExam2.setId(2L);
        sampleExam2.setSubject(subject2);

        List<Exam> exams = Arrays.asList(sampleExam, sampleExam2);

        // Setup
        when(examRepository.findAll())
            .thenReturn(exams);

        // Execute
        List<ExamResponse> examResponse = examServiceImpl.getAllExams();

        assertEquals(2, examResponse.size());

        // Varify
        verify(examRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Exam.class), eq(ExamResponse.class));

    }

    @Test
    void GetExamById_Existing_Return_ExamResponse() {
        
        // Setup
        when(examRepository.findById(anyLong()))
            .thenReturn(Optional.of(sampleExam));
        
        // when(examRepository.findByIdWithSubject(anyLong()))
        //         .thenReturn(Optional.of(sampleExam));

        // Execute
        ExamResponse examResponse = examServiceImpl.getExamById(1L);
        assertEquals(sampleExam.getId(), examResponse.getId());
        assertEquals(sampleExam.getSubject().getName() + " Exam", examResponse.getExamName());


        // Varify
        verify(examRepository, times(1)).findById(anyLong());
        verify(modelMapper, times(1)).map(any(Exam.class), eq(ExamResponse.class));
        // verify(examRepository, times(1)).findByIdWithSubject(anyLong());
    }

    @Test
    void getExamById_NonExisting_Throw_ResourceNotFoundException() {

        // Setup
        when(examRepository.findById(anyLong()))
            .thenReturn(Optional.empty());

        // Execute
        assertThrows(
            ResourceNotFoundException.class,
            () -> examServiceImpl.getExamById(1L)
        );

        // Varify
        verify(examRepository, times(1)).findById(anyLong());
        verify(modelMapper, never()).map(any(Exam.class), eq(ExamResponse.class));
    }


    @Test
    void deleteExam_Return_String_success() {
        // Setup
        when(examRepository.findById(anyLong()))
            .thenReturn(Optional.of(sampleExam));

        doNothing().when(examRepository).delete(any(Exam.class));

        // Execute
        String response = examServiceImpl.deregisterExam(sampleExam.getId());

        String expectedResponse = "Exam with id '" + sampleExam.getId() + "' deleted successfully";
        assertEquals(expectedResponse, response);

        // Varify
        verify(examRepository, times(1)).findById(anyLong());
        verify(examRepository, times(1)).delete(any(Exam.class));
    }

}