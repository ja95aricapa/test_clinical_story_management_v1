package com.example.clinicalhistoryapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.example.clinic", "com.example.clinicalhistoryapp"})
@EntityScan("com.example.clinic.model")
@EnableJpaRepositories("com.example.clinic.repository")
public class ClinicalHistoryAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClinicalHistoryAppApplication.class, args);
    }
}