package com.example.chefswipe.Cards;

import java.util.List;

public class Cards {
    private String recipeId;
    private String recipeName;
    private String recipeURL;


    public Cards(String recipeId, String recipeName, String recipeURL) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeURL = recipeURL;
    }

    //Getters & setters recipeId
    public String getRecipeId() {
      return recipeId;
    }
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    //Getters & setters recipeName
    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    //Getters & setters recipeName
    public String getRecipeURL() {
        return recipeURL;
    }
    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }

}
