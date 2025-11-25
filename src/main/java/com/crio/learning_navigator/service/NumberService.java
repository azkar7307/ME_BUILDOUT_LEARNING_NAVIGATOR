package com.crio.learning_navigator.service;

import com.crio.learning_navigator.dto.response.NumberResponse;

public interface NumberService {
    
    NumberResponse getNumberFact(Long number);
}
