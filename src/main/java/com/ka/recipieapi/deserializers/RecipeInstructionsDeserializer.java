package com.ka.recipieapi.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeInstructionsDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        List<String> instructions = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.has("text")) {
                    instructions.add(item.get("text").asText());
                } else if (item.isTextual()){
                    instructions.add(item.asText());
                }
            }
        }
        return instructions;
    }
}
