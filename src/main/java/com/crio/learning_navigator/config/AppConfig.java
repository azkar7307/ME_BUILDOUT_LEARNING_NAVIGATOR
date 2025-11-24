package com.crio.learning_navigator.config;

import java.util.List;
import com.crio.learning_navigator.dto.response.ExamResponse;
import com.crio.learning_navigator.dto.response.StudentResponse;
import com.crio.learning_navigator.dto.response.SubjectResponse;
import com.crio.learning_navigator.entity.Exam;
import com.crio.learning_navigator.entity.Student;
import com.crio.learning_navigator.entity.Subject;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 2 sec
        factory.setReadTimeout(5000);    // 5 sec
    
        return new RestTemplate(factory);
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper =  new ModelMapper();

        Converter<Exam, ExamResponse> examConverter = ctx -> {
            // System.out.println("INSIDE CONVERTER");
            
            Exam src = ctx.getSource();
            // System.out.println("INSIDE CONVERTER, get exam object");

            if (src == null) return null;
            
            String examName = (src.getSubject() != null && src.getSubject().getName() != null) 
                ? src.getSubject().getName() + " Exam" :
                "Unknown";
            return new ExamResponse(src.getId(), examName);
        };

        modelMapper.addConverter(examConverter, Exam.class, ExamResponse.class);
        
        TypeMap<Subject, SubjectResponse> subjectMap = modelMapper.createTypeMap(
            Subject.class, 
            SubjectResponse.class
        );

        subjectMap.addMappings(mapper -> {
            mapper.map(Subject::getId, SubjectResponse::setId);
            mapper.map(Subject::getName, SubjectResponse::setSubjectName);
        });

        // Create a type map
        TypeMap<Student, StudentResponse> studentMap = modelMapper.createTypeMap(
            Student.class, 
            StudentResponse.class
        );

        studentMap.addMappings(mapper -> {
            // System.out.println("inside student type map");
              // map subjects list
            mapper.map(
                src -> (src.getSubjects() != null)
                    ? src.getSubjects()
                        .stream()
                        .map(sub -> modelMapper.map(sub, SubjectResponse.class))
                        .toList()
                    : List.of(),
                StudentResponse::setEnrolledSubjects
            );
            
            // map exams list
            mapper.map(
                src -> (src.getExams() != null)
                    ? src.getExams()
                        .stream()
                        .map(exam -> modelMapper.map(exam, ExamResponse.class))
                        .toList()
                    : List.of(),
                StudentResponse::setEnrolledExams
            );   
             
            // System.out.println("After student type map");
        });

        return modelMapper;
    }
}