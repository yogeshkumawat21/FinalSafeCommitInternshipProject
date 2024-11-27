package com.App.Yogesh.ServiceImplmentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j  // Lombok annotation for logging
public class ThirdPartyApi {

    @Autowired
    private RestTemplate restTemplate;
    public String sendPostRequest(String apiUrl, Object requestBody) {
        // Log the incoming request and URL
        log.info("Sending POST request to: {} with body: {}", apiUrl, requestBody);

        try {
            // Set up HTTP headers
            HttpHeaders headers = new HttpHeaders();
            // You can add specific headers here (e.g., Authorization, Content-Type)
            headers.set("Content-Type", "application/json");  // Example: Set content type as JSON

            // Create HTTP entity with the request body and headers
            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

            // Send the POST request using RestTemplate.exchange
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            // Log the response status and body for debugging
            log.info("Response from API: {}", response.getBody());

            // Return the body of the response
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Log the error response if the API returns an error
            log.error("Error occurred while making POST request to {}. Status code: {}. Response: {}",
                    apiUrl, e.getStatusCode(), e.getResponseBodyAsString());

            // Return a formatted error message with the status code and response body
            return "Error: " + e.getStatusCode() + " " + e.getResponseBodyAsString();
        } catch (Exception e) {
            // Catch any other exceptions and log them
            log.error("Unexpected error occurred while making POST request to {}: {}", apiUrl, e.getMessage());
            return "Unexpected error: " + e.getMessage();
        }
    }
}
