package com.ka.recipieapi.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

//    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/engines/gpt-3.5-turbo-instruct/completions";

    public OpenAIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = "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";
    }

    public String getCompletion(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String initialPrompt = "Din jobb er å lese den gitt oppskriften og gjengi den som en json fil på det gitte formatet. Du skal ikke endre på noen av ingrediensene, mengdene eller instruksene. Her er json formatet du alltid må bruke: {name\": \"string\", \"portions\": number,\"ingredients\": [\"string\"],\"instructions\": [\"string\"]}. Oppskrift som json objekt: ";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");

        Map<String, String> responseFormat = new HashMap<>();
        responseFormat.put("type", "json_object");
        requestBody.put("response_format", responseFormat);


        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", initialPrompt + message);

        requestBody.put("messages", new Map[]{messageContent});
        requestBody.put("temperature", 0.0);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return extractContentFromResponse(response.getBody());
    }

    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response from OpenAI", e);
        }
    }
}

