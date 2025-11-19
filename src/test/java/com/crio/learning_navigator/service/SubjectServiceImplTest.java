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
import com.crio.learning_navigator.dto.SubjectDTO;
import com.crio.learning_navigator.dto.response.SubjectResponse;
import com.crio.learning_navigator.entity.Subject;
import com.crio.learning_navigator.exception.ResourceAlreadyExistException;
import com.crio.learning_navigator.exception.ResourceNotFoundException;
import com.crio.learning_navigator.repository.SubjectRepository;
import com.crio.learning_navigator.service.impl.SubjectServiceImpl;
import org.hibernate.dialect.function.array.ArraySetUnnestFunction;
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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SubjectServiceImplTest {
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectServiceImpl subjectServiceImpl;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    private SubjectDTO subjectDTO;
    private Subject sampleSubject;

    @BeforeEach
    void setup() {
        subjectDTO = new SubjectDTO();
        subjectDTO.setName("Java301");

        sampleSubject = modelMapper.map(subjectDTO, Subject.class);
        sampleSubject.setId(1L);
    }

    @Test
    void createSubject_Return_SubjectResponse() {
        // Setup
        when(subjectRepository.findByName(anyString())).thenReturn(null);
        when(subjectRepository.save(any(Subject.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        SubjectResponse subjectResponse = subjectServiceImpl.registerSubject(subjectDTO);

        // Varify
        assertEquals(subjectDTO.getName(), subjectResponse.getName());
        verify(subjectRepository, times(1)).findByName(anyString());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void createSubject_Duplicate_Throw_ResourceAlreadyExistException() {

        // Setup
        when(subjectRepository.findByName(anyString())).thenReturn(sampleSubject);

        // Execute
        assertThrows(
                ResourceAlreadyExistException.class,
                () -> subjectServiceImpl.registerSubject(subjectDTO)
        );

        // Verify
        verify(subjectRepository, times(1)).findByName(anyString());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    void GetAllSubjects_Return_ListOfSubjectResponse() {
        
        SubjectDTO subjectDTO2 = new SubjectDTO();
        subjectDTO2.setName("Java302");
        
        Subject sampleSubject2 = modelMapper.map(subjectDTO2, Subject.class);
        sampleSubject2.setId(2L);
        
        List<Subject> subjects = Arrays.asList(sampleSubject, sampleSubject2);
        
        // Setup
        when(subjectRepository.findAll())
                .thenReturn(subjects);

        // Execute
        List<SubjectResponse> subjectsResponse = subjectServiceImpl.getAllSubjects();
        assertEquals(2, subjectsResponse.size());

        // Varify
        verify(subjectRepository, times(1)).findAll();
    }

    @Test
    void GetSubjectById_Existing_Return_SubjectResponse() {
        // Setup
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleSubject));

        // Execute
        SubjectResponse subjectResponse = subjectServiceImpl.getSubjectById(1L);
        assertEquals(sampleSubject.getId(), subjectResponse.getId());
        assertEquals(sampleSubject.getName(), subjectResponse.getName());

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
    }

    @Test
    void getSubjectById_NonExisting_Throw_ResourceNotFoundException() {
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Execute
        assertThrows(
                ResourceNotFoundException.class,
                () -> subjectServiceImpl.getSubjectById(1L)
        );

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateSubject_Return_String_Success() {
        SubjectDTO subjectToUpdate = new SubjectDTO();
        subjectToUpdate.setName("Java301 BD");

        // Setup
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleSubject));

        when(subjectRepository.findByName(anyString()))
                .thenReturn(null);

        when(subjectRepository.save(any(Subject.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        // Execute
        String response = subjectServiceImpl.updateSubject(sampleSubject.getId(), subjectToUpdate);

        String expectedResponse = "Subject with id '" + sampleSubject.getId() + "' updated successfully.";
        assertEquals(expectedResponse, response);

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(subjectRepository, times(1)).findByName(anyString());
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    void updateSubject_WithExistingSubjectName_Throw_Exception() {
        SubjectDTO sampleSubject2 = new SubjectDTO();
        sampleSubject2.setName("Java301 BD");

        Subject existingSubject = modelMapper.map(sampleSubject2, Subject.class);
        existingSubject.setId(2L);

        // Setup
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleSubject));

        when(subjectRepository.findByName(anyString()))
                .thenReturn(existingSubject);

        // Execute
        // try to update sampleSubject
        assertThrows(
                ResourceAlreadyExistException.class,
                () -> subjectServiceImpl.updateSubject(sampleSubject.getId(), subjectDTO)
        );

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(subjectRepository, times(1)).findByName(anyString());
    }

    @Test
    void deleteSubject_Return_String_success() {
        // Setup
        when(subjectRepository.findById(anyLong()))
                .thenReturn(Optional.of(sampleSubject));

        doNothing().when(subjectRepository).delete(any(Subject.class));

        // Execute
        String response = subjectServiceImpl.deleteSubject(1L);

        String expectedResponse = "Subject with id '" + sampleSubject.getId() + "' deleted successfully.";
        assertEquals(expectedResponse, response);

        // Varify
        verify(subjectRepository, times(1)).findById(anyLong());
        verify(subjectRepository, times(1)).delete(any(Subject.class));
    }

}