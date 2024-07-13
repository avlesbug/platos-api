package com.ka.recipieapi.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeYieldDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        return extractYield(node);
    }

    private Object extractYield(JsonNode node) {
        if (node.isInt()) {
            return node.asInt();
        } else if (node.isTextual()) {
            return extractNumberFromString(node.asText());
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                if (element.isTextual()) {
                    return extractNumberFromString(element.asText());
                } else if (element.isInt()) {
                    return element.asInt();
                }
            }
        }
        return 4; // Default value if no number is found
    }

    private Integer extractNumberFromString(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 4; // Default value if no number is found
    }
}
