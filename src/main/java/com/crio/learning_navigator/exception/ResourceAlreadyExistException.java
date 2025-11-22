package com.crio.learning_navigator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends RuntimeException{

  public ResourceAlreadyExistException(String message) {
    super(message);
  }
  
  public ResourceAlreadyExistException(Long id, String resourceName) {
    super(resourceName + " with id '" + id + "' already exist.");
  }

  public ResourceAlreadyExistException(String resourceId, String resourceName) {
    super(resourceName + " '" + resourceId + "' already exist.");
  }
    
}