package com.example.lifesworkiguess;

public class Step {

    private String name, description, time, action;
    private int number;


    public Step(String name, String description, String time, String action){
        this.name = name;
        this.description = description;
        this.time = time;
        this.action = action;

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
