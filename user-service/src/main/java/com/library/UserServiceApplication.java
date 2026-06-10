package com.library;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            userRepository.save(User.builder()
                    .name("Ana García")
                    .email("ana.garcia@email.com")
                    .phone("600111222")
                    .status(User.UserStatus.ACTIVE)
                    .build());
            userRepository.save(User.builder()
                    .name("Carlos López")
                    .email("carlos.lopez@email.com")
                    .phone("600333444")
                    .status(User.UserStatus.ACTIVE)
                    .build());
        };
    }
}
