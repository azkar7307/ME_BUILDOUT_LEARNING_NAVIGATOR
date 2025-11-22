package com.crio.learning_navigator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  
  public ResourceNotFoundException(Long Id, String resourceName) {
    super(resourceName + " with ID '" + Id + "' not found");
  }
}