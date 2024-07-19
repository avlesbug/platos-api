package com.ka.recipieapi.deserializers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.ka.recipieapi.types.IngredientObject;
import com.ka.recipieapi.types.RecipeDto;
import com.ka.recipieapi.types.RecipeJson;

public class RecipeIngredientsParser {

    public static IngredientObject parseIngredients(String ingredientString) {
        IngredientObject parsedIngredient;

        // Regular expression patterns to match different types of quantities
        String rangePattern = "^(\\d+\\s*[-–]\\s*\\d+\\s*[a-zA-Z]*)\\s+(.*)$";
        String fractionPattern = "^([\\d/]+\\s*[a-zA-Z]*)\\s+(.*)$";
        String approxPattern = "^(ca\\.\\s*\\d+\\s*[a-zA-Z]*)\\s+(.*)$";
        String simplePattern = "^(\\d+\\s*[a-zA-Z]*)\\s+(.*)$";

        // Compile patterns
        Pattern rangeRegex = Pattern.compile(rangePattern);
        Pattern fractionRegex = Pattern.compile(fractionPattern);
        Pattern approxRegex = Pattern.compile(approxPattern);
        Pattern simpleRegex = Pattern.compile(simplePattern);

        // Matcher to find matches
        Matcher rangeMatcher = rangeRegex.matcher(ingredientString.trim());
        Matcher fractionMatcher = fractionRegex.matcher(ingredientString.trim());
        Matcher approxMatcher = approxRegex.matcher(ingredientString.trim());
        Matcher simpleMatcher = simpleRegex.matcher(ingredientString.trim());

        // Check for matches and extract quantity and ingredient name
        if (rangeMatcher.matches()) {
            parsedIngredient = new IngredientObject(rangeMatcher.group(1).trim(), rangeMatcher.group(2).trim());
        } else if (fractionMatcher.matches()) {
            parsedIngredient = new IngredientObject(fractionMatcher.group(1).trim(), fractionMatcher.group(2).trim());
        } else if (approxMatcher.matches()) {
            parsedIngredient = new IngredientObject(approxMatcher.group(1).trim(), approxMatcher.group(2).trim());
        } else if (simpleMatcher.matches()) {
            parsedIngredient = new IngredientObject(simpleMatcher.group(1).trim(), simpleMatcher.group(2).trim());
        } else {
            // Default case where no quantity is matched
            parsedIngredient = new IngredientObject(null, ingredientString.trim());
        }

        return parsedIngredient;
    }

}
