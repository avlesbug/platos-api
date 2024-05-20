package com.ka.recipieapi.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecipieController {
    private OpenAIService openAIService;
    private RecipeCollectorService recipeCollectorService;

    public RecipieController(OpenAIService openAIService, RecipeCollectorService recipeCollectorService) {
        this.openAIService = openAIService;
        this.recipeCollectorService = recipeCollectorService;
    }


    @GetMapping("api/recipe")
    public String getCompletion(@RequestParam String prompt) {
        try {
            return openAIService.getCompletion(recipeCollectorService.getPlainTextFromUrl(prompt));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/api/ping")
    public String getRecipeFromURL(){
        return "This will be a recipe in the future";
    }
}
