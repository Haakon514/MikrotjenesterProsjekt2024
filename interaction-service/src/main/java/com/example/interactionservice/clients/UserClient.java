package com.example.interactionservice.clients;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@AllArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    //check if user exists in the user-service
    public Boolean checkUserExist(Long userId){

        String USER_SERVICE_URL = "http://localhost:8081/user-service/users/get/";

        try {
            String url = USER_SERVICE_URL + userId;
            return restTemplate.getForObject(url, Boolean.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("an error occurred, user not found", e);
            return false;
        } catch (Exception e) {
            log.info("an error occurred", e);
            return false;
        }
    }
}

