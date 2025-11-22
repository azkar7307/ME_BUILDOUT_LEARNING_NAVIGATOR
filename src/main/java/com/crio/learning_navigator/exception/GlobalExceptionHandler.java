package com.crio.learning_navigator.exception;

import com.crio.learning_navigator.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex, 
        WebRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), status, request);
        log.error("Resource Not Found - {}", errorResponse);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // @ExceptionHandler({
    //     ResourceAlreadyExistException.class,
    //     StudentNotEnrolledInSubjectException.class
    // })
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExist(
        ResourceAlreadyExistException ex, 
        WebRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), status, request);
        log.warn("CONFLICT - {}", errorResponse);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex, 
        WebRequest request
    ) {
        String allErrorsMsg = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining("; "));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = buildErrorResponse(allErrorsMsg, status, request);
        log.warn("Validation failed - {}", errorResponse); 
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
        HttpMessageNotReadableException ex,
        WebRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMsg = getErrorMessages(ex.getMostSpecificCause());
        ErrorResponse errorResponse = buildErrorResponse(errorMsg, status, request);
        log.warn("Invalid JSON request: - {}", errorResponse); 
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex,
        WebRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = buildErrorResponse(ex.getMessage(), status, request);
        log.error("Internal server error - {}.", errorResponse);
        return ResponseEntity.status(status).body(errorResponse);  
    }

    private ErrorResponse buildErrorResponse(
        String msg,
        HttpStatus status,
        WebRequest request
    ) {  
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(msg)
            .path(((ServletWebRequest) request).getRequest().getRequestURI())
            .build();
    }

    private String getErrorMessages(Throwable cause) {
        if (cause instanceof MismatchedInputException) {
        MismatchedInputException mie = (MismatchedInputException) cause;
        String path = mie.getPath().stream()
            .map(JsonMappingException.Reference::getFieldName)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("."));
        return String.format("Field '%s' has invalid value: %s", path, mie.getOriginalMessage());
        }
        return "Malformed JSON request: " + cause.getMessage();
    }
}
