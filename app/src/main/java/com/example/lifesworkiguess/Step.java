package com.example.lifesworkiguess;

public class Step {

    private String name, description, time, action; //Action is also called a step's "Image" and vice versa, both "action" and "image" mean the same thing.
    private int number;  //A STEP's NUMBER is its INDEX in the stepList.


    public Step(String name, String description, String time, String action){
        this.name = name;
        this.description = description;
        this.time = time;
        this.action = action;

    }


    //For Community Created Recipes:
    public Step(String name, String description, String time, String action, int number) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.action = action;
        this.number = number;
    }

    //For Community Created Recipes:
    public Step(String name, String description, String time, int number) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.number = number;
        this.action = MyConstants.NO_IMAGE_FOR_STEP;
    }

    public String getAction() {
        return action;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
