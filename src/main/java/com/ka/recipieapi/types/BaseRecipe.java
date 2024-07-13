package com.ka.recipieapi.types;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BaseRecipe {
    private String name;
    private int portions;
    private String totalTime;
    private List<String> ingredients;
    private List<String> instructions;

    // Default constructor
    public BaseRecipe() {
    }

    // Parameterized constructor
    @JsonCreator
    public BaseRecipe(@JsonProperty("name") String name,
        @JsonProperty("portions") int portions,
        @JsonProperty("totalTime") String totalTime,
        @JsonProperty("ingredients") List<String> ingredients,
        @JsonProperty("instructions") List<String> instructions) {
        this.name = name;
        this.portions = portions;
        this.totalTime = totalTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public RecipeDto toRecipeDto(String imageUrl) {
        return RecipeDto.builder()
            .name(name)
            .recipeYield(portions)
            .totalTime(totalTime)
            .recipeIngredient(ingredients)
            .recipeInstructions(instructions)
            .image(imageUrl)
            .build();
    }
}
