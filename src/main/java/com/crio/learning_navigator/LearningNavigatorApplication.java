package com.crio.learning_navigator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@SpringBootApplication
@RestController
public class LearningNavigatorApplication {

	public static void main(String[] args) {
		log.info("LEARNING NAVIGATOR APPLICATION STARTS!!!");
		SpringApplication.run(LearningNavigatorApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		log.info("LEARNING NAVIGATOR APPLICATION works gracefully");
		return "WELCOME TO LEARNING NAVIGATOR APPLICATION";
	}
}
