package com.ka.recipieapi.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ka.recipieapi.deserializers.ImageDeserializer;
import com.ka.recipieapi.deserializers.RecipeInstructionsDeserializer;
import com.ka.recipieapi.deserializers.RecipeYieldDeserializer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeJson {
    private String name;
    @JsonDeserialize(using = RecipeYieldDeserializer.class)
    private Object portions;
    private List<String> recipeIngredient;
    @JsonDeserialize(using = RecipeInstructionsDeserializer.class)
    private List<String> recipeInstructions;
    @JsonDeserialize(using = ImageDeserializer.class)
    private String image;

    public RecipeJson() {
    }

    @JsonCreator
    public RecipeJson(@JsonProperty("name") String name,
        @JsonProperty("recipeYield") @JsonDeserialize(using = RecipeYieldDeserializer.class) Object recipeYield,
        @JsonProperty("ingredients") List<String> recipeIngredient,
        @JsonProperty("instructions") @JsonDeserialize(using = RecipeInstructionsDeserializer.class)
        List<String> recipeInstructions,
        @JsonProperty("image") @JsonDeserialize(using = ImageDeserializer.class) String image) {
        this.name = name;
        this.portions = recipeYield;
        this.recipeIngredient = recipeIngredient;
        this.recipeInstructions = recipeInstructions;
        this.image = image;
    }
}
