package com.library.client;

import com.library.dto.UserDto;
import com.library.exception.ServiceCommunicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${services.user-service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public UserDto getUserById(Long userId) {
        try {
            return restTemplate.getForObject(userServiceUrl + "/api/users/" + userId, UserDto.class);
        } catch (Exception e) {
            log.error("Error fetching user {}: {}", userId, e.getMessage());
            throw new ServiceCommunicationException("Could not reach user-service: " + e.getMessage());
        }
    }
}
