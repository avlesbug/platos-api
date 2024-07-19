package com.ka.recipieapi.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.parser.Parser;

public class RecipeInstructionsDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        List<String> instructions = new ArrayList<>();
        extractInstructionsRecursive(node, instructions);
        return instructions;
    }

    private static void extractInstructionsRecursive(JsonNode node, List<String> instructions) {
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.has("text")) {
                    instructions.add(
                    Parser.unescapeEntities(
                        item.get("text").asText(), true));
                } else if (item.isTextual()) {
                    instructions.add(
                        Parser.unescapeEntities(item.asText(), true));
                } else if (item.has("itemListElement")) {
                    extractInstructionsRecursive(item.get("itemListElement"), instructions);
                }
            }
        } else if (node.has("text")) {
            instructions.add(
                Parser.unescapeEntities(
                    node.get("text").asText(), true));
        } else if (node.isTextual()) {
            instructions.add(
                Parser.unescapeEntities(node.asText(), true));
        } else if (node.has("itemListElement")) {
            extractInstructionsRecursive(node.get("itemListElement"), instructions);
        }
    }
}
