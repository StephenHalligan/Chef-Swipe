package com.example.chefswipe.SavedRecipes;

public class SavedObject {

    private String recipeID;
    private String recipeURL;
    private String recipeName;

    public SavedObject (String recipeID, String recipeURL, String recipeName) {
        this.recipeID = recipeID;
        this.recipeURL = recipeURL;
        this.recipeName= recipeName;
    }

    //Getters & setters recipeName
    public String getRecipeID() {
        return recipeID;
    }
    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    //Getters & setters recipeURL
    public String getRecipeURL() {
        return recipeURL;
    }
    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }


    //Getters & setters recipeURL
    public String getRecipeName() {
        return recipeName;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }


}
