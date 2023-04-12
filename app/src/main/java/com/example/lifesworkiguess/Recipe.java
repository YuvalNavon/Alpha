package com.example.lifesworkiguess;

import java.util.ArrayList;

public class Recipe {

    private String title;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;


    public Recipe (String title){
        this.title = title;
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
    }

    public Recipe(String title, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }




    public void addIngredient(Ingredient ingredient){
        this.ingredients.add(ingredient);
    }
    public void addStep(Step step){
        this.steps.add(step);
        step.setNumber(steps.size()-1);

    }

}
