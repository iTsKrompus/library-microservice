package com.library.client;

import com.library.dto.BookDto;
import com.library.exception.ServiceCommunicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class BookClient {

    private final RestTemplate restTemplate;
    private final String bookServiceUrl;

    public BookClient(RestTemplate restTemplate,
                      @Value("${services.book-service.url}") String bookServiceUrl) {
        this.restTemplate = restTemplate;
        this.bookServiceUrl = bookServiceUrl;
    }

    public BookDto getBookById(Long bookId) {
        try {
            return restTemplate.getForObject(bookServiceUrl + "/api/books/" + bookId, BookDto.class);
        } catch (Exception e) {
            log.error("Error fetching book {}: {}", bookId, e.getMessage());
            throw new ServiceCommunicationException("Could not reach book-service: " + e.getMessage());
        }
    }

    public BookDto decreaseAvailableCopies(Long bookId) {
        try {
            // CORRECCIÓN: Cambiado patchForObject por postForObject para evitar la limitación de la JDK
            return restTemplate.postForObject(
                    bookServiceUrl + "/api/books/" + bookId + "/decrease-copies",
                    null, BookDto.class);
        } catch (Exception e) {
            log.error("Error decreasing copies for book {}: {}", bookId, e.getMessage());
            throw new ServiceCommunicationException("Could not update book copies: " + e.getMessage());
        }
    }

    public BookDto increaseAvailableCopies(Long bookId) {
        try {
            // CORRECCIÓN: Cambiado patchForObject por postForObject para evitar la limitación de la JDK
            return restTemplate.postForObject(
                    bookServiceUrl + "/api/books/" + bookId + "/increase-copies",
                    null, BookDto.class);
        } catch (Exception e) {
            log.error("Error increasing copies for book {}: {}", bookId, e.getMessage());
            throw new ServiceCommunicationException("Could not update book copies: " + e.getMessage());
        }
    }
}