package com.library;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(BookRepository bookRepository) {
        return args -> {
            bookRepository.save(Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("9780132350884")
                    .publishedYear(2008)
                    .totalCopies(5)
                    .availableCopies(5)
                    .build());
            bookRepository.save(Book.builder()
                    .title("The Pragmatic Programmer")
                    .author("Andrew Hunt")
                    .isbn("9780201616224")
                    .publishedYear(1999)
                    .totalCopies(3)
                    .availableCopies(3)
                    .build());
            bookRepository.save(Book.builder()
                    .title("Design Patterns")
                    .author("Gang of Four")
                    .isbn("9780201633610")
                    .publishedYear(1994)
                    .totalCopies(4)
                    .availableCopies(4)
                    .build());
        };
    }
}
