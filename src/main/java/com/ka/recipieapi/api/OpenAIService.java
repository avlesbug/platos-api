package com.ka.recipieapi.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ka.recipieapi.types.BaseRecipe;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${open.ai.key}")
    private String OPENAI_API_KEY;

    @Value("${open.ai.url}")
    private String OPENAI_API_URL;

    private String OPENAI_API_PROMPT = "Din jobb er aa lese den gitt oppskriften og gjengi den som en json fil på det gitte formatet.\\\n"
        + "  Du skal ikke endre på noen av ingrediensene, mengdene eller instruksene. \\\n"
        + "  Her er json formatet du alltid maa bruke: {name\": \"string\", \"portions\": number, \"totalTime\": number,\"ingredients\": [\"string\"],\"instructions\": [\"string\"]}. \\\n"
        + "  Oppskrift som json objekt:";


    public OpenAIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public BaseRecipe getCompletion(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");

        Map<String, String> responseFormat = new HashMap<>();
        responseFormat.put("type", "json_object");
        requestBody.put("response_format", responseFormat);


        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "user");
        messageContent.put("content", OPENAI_API_PROMPT + message);

        requestBody.put("messages", new Map[]{messageContent});
        requestBody.put("temperature", 0.0);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, entity, String.class);

        String recipeAsString = extractContentFromResponse(response.getBody());

        System.out.println("Response: " + response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        BaseRecipe recipe = null;
        try {
            recipe = objectMapper.readValue(recipeAsString, BaseRecipe.class);
            System.out.println(recipe);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipe;
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

