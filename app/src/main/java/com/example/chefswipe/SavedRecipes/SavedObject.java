package com.example.chefswipe.SavedRecipes;

public class SavedObject {

    private String recipeName;
    private String recipeURL;

    public SavedObject (String recipeName, String recipeURL) {
        this.recipeName = recipeName;
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


}
