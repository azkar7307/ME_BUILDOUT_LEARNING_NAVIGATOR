package com.crio.learning_navigator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.crio.learning_navigator.service.NumberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/easter-egg/hidden-feature")
public class NumberController {
    
    private final NumberService numberService;

    @GetMapping("/{random_number}")
    public ResponseEntity<String> getNumberFact(@PathVariable("random_number") Long number) {
        log.info("Request received to generate random number facts, number: {}", number);
        String response = numberService.getNumberFact(number);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
