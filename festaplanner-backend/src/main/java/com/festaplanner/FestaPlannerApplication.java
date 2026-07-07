package com.festaplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao Festa Planner.
 * Backend Spring Boot que atende ao frontend Angular (festaPlanner-main).
 */
@SpringBootApplication
public class FestaPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FestaPlannerApplication.class, args);
    }
}
