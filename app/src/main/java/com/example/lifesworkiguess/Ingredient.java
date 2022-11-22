package com.example.lifesworkiguess;

public class Ingredient {

    private String name,amount,  units;

    public Ingredient(String name,  String amount, String units){
        this.name = name;
        this.amount = amount;
        this.units = units;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
