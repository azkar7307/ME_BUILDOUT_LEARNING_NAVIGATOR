package com.crio.learning_navigator.config;

import lombok.extern.slf4j.Slf4j;
import com.crio.learning_navigator.dto.response.ExamResponse;
import com.crio.learning_navigator.entity.Exam;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper =  new ModelMapper();

        Converter<Exam, ExamResponse> examConverter = ctx -> {
            // System.out.println("INSIDE CONVERTER");
            
            Exam src = ctx.getSource();
            // System.out.println("INSIDE CONVERTER, get exam object");

            if (src == null) return null;
            
            String examName = src.getSubject().getName() + " Exam";
            return new ExamResponse(src.getId(), examName);
        };

        modelMapper.addConverter(examConverter, Exam.class, ExamResponse.class);
        return modelMapper;
    }
}