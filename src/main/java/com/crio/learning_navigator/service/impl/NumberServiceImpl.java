package com.crio.learning_navigator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.crio.learning_navigator.service.NumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NumberServiceImpl implements NumberService {

    private final RestTemplate restTemplate;
    private static final String NUMBERS_API_URl = "http://numbersapi.com/{random_number}";
    
    @Override
    public String getNumberFact(Long number) {
        log.info("Random Number Facts generating... for number {}", number);
        ResponseEntity<String> response = restTemplate.getForEntity(NUMBERS_API_URl, String.class, number);
        return response.getBody();
    }
    
}
