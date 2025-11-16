package com.crio.learning_navigator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends RuntimeException{

  public ResourceAlreadyExistException(String userId, String resourceName) {
    super(resourceName + " with '" + userId + "' already exist.");
  }
    
}