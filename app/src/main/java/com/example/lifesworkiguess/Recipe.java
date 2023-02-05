package com.example.lifesworkiguess;

import java.util.ArrayList;

public class Recipe {

    private String title;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private int ServeCount;
    private String Time;
    private String Difficulty;
    private boolean Kosher;

    public Recipe(String title, int serveCount){
            this.title = title;
            ingredients = new ArrayList<>();
            steps = new ArrayList<>();
            ServeCount = serveCount;


    }

    public Recipe (String title){
        this.title = title;
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
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


    public int getServeCount() {
        return ServeCount;
    }

    public void setServeCount(int serveCount) {
        ServeCount = serveCount;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

    public boolean isKosher() {
        return Kosher;
    }

    public void setKosher(boolean kosher) {
        Kosher = kosher;
    }

    public void addIngredient(Ingredient ingredient){
        this.ingredients.add(ingredient);
    }
    public void addStep(Step step){
        this.steps.add(step);
        step.setNumber(steps.size()-1);

    }

}
