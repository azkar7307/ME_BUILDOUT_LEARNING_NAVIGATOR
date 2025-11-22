package com.crio.learning_navigator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StudentNotEnrolledInSubjectException extends RuntimeException{
    public StudentNotEnrolledInSubjectException(String message) {
        super(message);
    }
}
