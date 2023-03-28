package com.example.chefswipe.Cards;

public class Cards {

    private String recipeId;
    private String recipeName;
    private String recipeURL;
    private String recipeTags;
    private String recipeAuthor;
    private Integer recipeLikes;
    private String recipeDesc;


    public Cards(String recipeId, String recipeName, String recipeURL, String recipeTags, String recipeAuthor, int recipeLikes, String recipeDesc) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeURL = recipeURL;
        this.recipeTags = recipeTags;
        this.recipeAuthor = recipeAuthor;
        this.recipeLikes = recipeLikes;
        this.recipeDesc = recipeDesc;
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

    //Getters & setters recipeAuthor
    public String getRecipeAuthor() {
        return recipeAuthor;
    }
    public void setRecipeAuthor(String recipeAuthor) {
        this.recipeAuthor = recipeAuthor;
    }

    //Getters & setters recipeLikes
    public Integer getRecipeLikes() {
        return recipeLikes;
    }
    public void setRecipeLikes(Integer recipeLikes) {
        this.recipeLikes = recipeLikes;
    }

    //Getters & setters recipeDesc
    public String getRecipeDesc() {
        return recipeDesc;
    }
    public void setRecipeDesc(String recipeDesc) {
        this.recipeDesc = recipeDesc;
    }


}
