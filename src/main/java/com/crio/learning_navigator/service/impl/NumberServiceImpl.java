package com.crio.learning_navigator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.crio.learning_navigator.dto.response.NumberResponse;
import com.crio.learning_navigator.service.NumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NumberServiceImpl implements NumberService {

    private final RestTemplate restTemplate;
    private static final String NUMBERS_API_URl = "http://numbersapi.com/{random_number}";
    
    @Override
    public NumberResponse getNumberFact(Long number) {
        log.info("Random Number Facts generating... for number {}", number);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                NUMBERS_API_URl,
                String.class, 
                number
            );
            NumberResponse numberResponse = new NumberResponse();
            numberResponse.setMessage("Great! You have found the hidden number fact");
            numberResponse.setResponse(response.getBody());
            log.info("Great! You have found the hidden number fact");
            return numberResponse;
        } catch (ResourceAccessException e) {
            log.info("Opps! Numbers API timeout detected, returning fallback response.");
            return new NumberResponse(
                "Opps! Numbers API timeout detected - unable to retrieve the hidden number fact.",  
                "Returning fallback response."
            );
        }
    }
    
}
