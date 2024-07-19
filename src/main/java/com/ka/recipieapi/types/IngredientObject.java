package com.ka.recipieapi.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IngredientObject {
    private Integer quantity;
    private String ingredient;

    @JsonCreator
    public IngredientObject(@JsonProperty("quantity") Integer quantity, @JsonProperty("ingredient") String ingredient) {
        this.quantity = quantity;
        this.ingredient = ingredient;
    }
}
