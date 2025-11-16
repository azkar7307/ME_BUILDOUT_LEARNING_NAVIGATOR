package com.crio.learning_navigator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String userId, String resourceName) {
    super(resourceName + " with ID '" + userId + "' not found.");
  }
}