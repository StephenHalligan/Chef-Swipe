package com.example.chefswipe.PublishedRecipes;

public class PublishedObject {

    private String recipeName;
    private String recipeID;
    private String recipeURL;

    //Getters
    public PublishedObject(String recipeName, String recipeURL, String recipeID) {
        this.recipeName = recipeName;
        this.recipeID = recipeID;
        this.recipeURL = recipeURL;
    }

    //Getters & setters recipeName
    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    //Getters & setters recipeURL
    public String getRecipeURL() {
        return recipeURL;
    }
    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }

    //Getters & setters recipeID
    public String getRecipeID() {
        return recipeID;
    }
    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }


}
