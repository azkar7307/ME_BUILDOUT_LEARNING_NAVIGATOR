package com.crio.learning_navigator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.crio.learning_navigator.config.AppConfig;
import com.crio.learning_navigator.dto.StudentDTO;
import com.crio.learning_navigator.dto.SubjectDTO;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.entity.Exam;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.StudentNotEnrolledInSubjectException;
import com.crio.learning_navigator.repository.ExamRepository;
import com.crio.learning_navigator.repository.StudentRepository;
import com.crio.learning_navigator.repository.SubjectRepository;
import com.crio.learning_navigator.service.impl.StudentEnrollmentServiceImpl;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class StudentEnrollmentServiceImplTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private ExamRepository examRepository;

    @Spy
    private ModelMapper modelMapper = new AppConfig().modelMapper();

    @InjectMocks
    private StudentEnrollmentServiceImpl studentEnrollmentServiceImpl;

    private StudentDTO studentDTO;
    private Student sampleStudent;

    private SubjectDTO subjectDTO;
    private Subject sampleSubject;
    private Exam sampleExam;


    //    private StudentResponse studentResponse;
    @BeforeEach
    void setup() {
        studentDTO = new StudentDTO();
        studentDTO.setName("Student1");
        studentDTO.setEmail("student1@crio.in");

        sampleStudent = modelMapper.map(studentDTO, Student.class);
        sampleStudent.setId(1L);

        subjectDTO = new SubjectDTO();
        subjectDTO.setSubjectName("Java301");

        sampleSubject = modelMapper.map(subjectDTO, Subject.class);
        sampleSubject.setId(1L);

        sampleExam = new Exam();
        sampleExam.setId(1L);
        sampleExam.setSubject(sampleSubject);
    }

    @Test
    void enrollStudentInSubject_Return_StudentResponse() {
        // Setup
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(sampleStudent));
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(sampleSubject));


        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        StudentResponse studentResponse = studentEnrollmentServiceImpl.enrollStudentInSubject(
            sampleStudent.getId(), 
            sampleSubject.getId()
        );
        assertEquals(studentDTO.getName(), studentResponse.getName());
        assertNotNull(studentResponse.getEnrolledSubjects());
        assertFalse(studentResponse.getEnrolledSubjects().isEmpty());
        assertTrue(studentResponse.getEnrolledExams().isEmpty());

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(modelMapper, times(1)).map(any(Student.class), eq(StudentResponse.class));
    }

    @Test
    void enrollStudentInSubject_AlreadyEnrolled_Throw_ResourceAlreadyExistException() {

        // enroll a student in a subject
        Set<Subject> enrolledSubject = new HashSet<>(Arrays.asList(sampleSubject));
        sampleStudent.setSubjects(enrolledSubject);


        // Setup
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(sampleStudent));
        when(subjectRepository.findById(anyLong())).thenReturn(Optional.of(sampleSubject));

        // Execute
        assertThrows(
            ResourceAlreadyExistException.class,
            () -> studentEnrollmentServiceImpl.enrollStudentInSubject(1L, 1L)
        );

        // Verify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, never()).save(any(Student.class));
        verify(modelMapper, never()).map(any(Student.class), eq(StudentResponse.class));
    }

    @Test
    void enrollStudentInExam_Return_StudentResponse() {

        // enroll a student in a subject
        Set<Subject> enrolledSubject = new HashSet<>(Arrays.asList(sampleSubject));
        sampleStudent.setSubjects(enrolledSubject);

        // Setup
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(sampleStudent));
        when(examRepository.findById(anyLong())).thenReturn(Optional.of(sampleExam));

        when(studentRepository.save(any(Student.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponse studentResponse =  studentEnrollmentServiceImpl.enrollStudentInExam(
            sampleStudent.getId(),
            sampleExam.getId()
        );
        assertEquals(1, studentResponse.getEnrolledExams().size());
        assertFalse(studentResponse.getEnrolledSubjects().isEmpty());
        assertFalse(studentResponse.getEnrolledExams().isEmpty());


        // Verify
        verify(examRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(modelMapper, times(1)).map(any(Student.class), eq(StudentResponse.class));
    }

    @Test
    void enrollStudentInExam_AlreadyEnrolledIn_Throw_ResourceAlreadyExistException() {

        // enroll a student in a exam
        Set<Exam> enrolledExams = new HashSet<>(Arrays.asList(sampleExam));
        sampleStudent.setExams(enrolledExams);

        // Setup
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(sampleStudent));
        when(examRepository.findById(anyLong())).thenReturn(Optional.of(sampleExam));

        // Execute
        assertThrows(
            ResourceAlreadyExistException.class,
            () -> studentEnrollmentServiceImpl.enrollStudentInExam(1L, 1L)
        );

        // Verify
        verify(examRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, never()).save(any(Student.class));
        verify(modelMapper, never()).map(any(Student.class), eq(StudentResponse.class));
    }

    @Test
    void enrollStudentInExam_NotEnrolledInSubject_Throw_StudentNotEnrolledInSubjectException() {

        // Setup
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(sampleStudent));
        when(examRepository.findById(anyLong())).thenReturn(Optional.of(sampleExam));

        // Execute
        assertThrows(
            StudentNotEnrolledInSubjectException.class,
            () -> studentEnrollmentServiceImpl.enrollStudentInExam(1L, 1L)
        );

        // Verify
        verify(examRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, never()).save(any(Student.class));
        verify(modelMapper, never()).map(any(Student.class), eq(StudentResponse.class));
    }

}
