package com.ka.recipieapi.deserializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.parser.Parser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.ka.recipieapi.types.IngredientObject;

public class RecipeIngredientsDeserializer extends JsonDeserializer<List<IngredientObject>> {;
    @Override
    public List<IngredientObject> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        List<IngredientObject> ingredients = new ArrayList<>();
        extractInstructionsRecursive(node, ingredients);
//        List<IngredientObject> ingredients = new ArrayList<>();
//        for(String instruction : instructions) {
//            ingredients.add(new IngredientObject(4,instruction));
//        }
        return ingredients;
    }

    private void extractInstructionsRecursive(JsonNode node, List<IngredientObject> instructions) {
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.has("text")) {
                    instructions.add(
                        RecipeIngredientsParser.parseIngredients(
                        Parser.unescapeEntities(
                            item.get("text").asText(), true)));
                } else if (item.isTextual()) {
                    instructions.add(
                        RecipeIngredientsParser.parseIngredients(
                        Parser.unescapeEntities(item.asText(), true)));
                } else if (item.has("itemListElement")) {
                    extractInstructionsRecursive(item.get("itemListElement"), instructions);
                }
            }
        } else if (node.has("text")) {
            instructions.add(
                RecipeIngredientsParser.parseIngredients(
                Parser.unescapeEntities(
                    node.get("text").asText(), true)));
        } else if (node.isTextual()) {
            instructions.add(
                RecipeIngredientsParser.parseIngredients(
                Parser.unescapeEntities(node.asText(), true)));
        } else if (node.has("itemListElement")) {
            extractInstructionsRecursive(node.get("itemListElement"), instructions);
        }
    }
}
