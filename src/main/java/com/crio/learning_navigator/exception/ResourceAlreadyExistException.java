package com.crio.learning_navigator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends RuntimeException{

  public ResourceAlreadyExistException(Long userId, String resourceName) {
    super(resourceName + " with id '" + userId + "' already exist.");
  }

  public ResourceAlreadyExistException(String email, String resourceName) {
    super(resourceName + " with email '" + email + "' already exist.");
  }
    
}