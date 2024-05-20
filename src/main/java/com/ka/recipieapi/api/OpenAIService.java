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

//    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/engines/gpt-3.5-turbo-instruct/completions";

    public OpenAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.apiKey = "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";
    }

    public String getCompletion(String prompt) throws IOException {
        String initialPromp = "Din jobb er å lese den gitt oppskriften og gjengi den som en json fil på det gitte formatet. Du skal ikke endre på noen av ingrediensene, mengdene eller instruksene. Her er json formatet du alltid må bruke: {name\": \"string\", \"portions\": number,\"ingredients\": [\"string\"],\"instructions\": [\"string\"]}. Oppskrift: ";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> body = new HashMap<>();
        body.put("prompt", initialPromp + prompt + " Oppskriften som json: ");
        body.put("max_tokens", 2900);  // Specify the maximum number of tokens to generate

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            OPENAI_API_URL, HttpMethod.POST, entity, String.class);

        // Parse the JSON response to extract the first choice
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        JsonNode choicesNode = rootNode.path("choices");

        if (choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode firstChoice = choicesNode.get(0);
            return firstChoice.path("text").asText();
        } else {
            throw new IOException("Unexpected response format");
        }
    }
}

