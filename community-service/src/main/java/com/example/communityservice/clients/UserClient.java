package com.example.communityservice.clients;

import com.example.communityservice.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    //check if user exists in the user service
    public UserResponseDTO getUserFromUserService(Long id){

        String USER_SERVICE_URL = "http://localhost:8081/user-service/users/get/";

        try {
            String url = USER_SERVICE_URL + id;
            return restTemplate.getForObject(url, UserResponseDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("an error occurred, user not found", e);
            return null;
        } catch (Exception e) {
            log.info("an error occurred", e);
            return null;
        }
    }
}
