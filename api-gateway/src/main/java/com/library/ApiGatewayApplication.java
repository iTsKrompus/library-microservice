package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

@RestController
class FallbackController {

    @GetMapping("/fallback/books")
    public Mono<Map<String, String>> booksFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "Book Service is currently unavailable. Please try again later."
        ));
    }

    @GetMapping("/fallback/users")
    public Mono<Map<String, String>> usersFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "User Service is currently unavailable. Please try again later."
        ));
    }

    @GetMapping("/fallback/loans")
    public Mono<Map<String, String>> loansFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "Loan Service is currently unavailable. Please try again later."
        ));
    }
}
