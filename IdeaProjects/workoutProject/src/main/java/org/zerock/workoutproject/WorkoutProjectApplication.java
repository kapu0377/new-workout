package org.zerock.workoutproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WorkoutProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkoutProjectApplication.class, args);
    }

}