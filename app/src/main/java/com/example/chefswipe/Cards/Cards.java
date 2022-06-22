package com.example.chefswipe.Cards;

public class Cards {

    private String recipeId;
    private String recipeName;
    private String recipeURL;
    private String recipeTags;

    public Cards(String recipeId, String recipeName, String recipeURL, String recipeTags) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeURL = recipeURL;
        this.recipeTags = recipeTags;
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

    //Getters & setters recipeTags
   public String getRecipeTags() {
        return recipeTags;
    }
    public void setRecipeTags(String recipeTags) {
        this.recipeTags = recipeTags;
    }


}
