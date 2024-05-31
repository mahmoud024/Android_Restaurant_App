package com.example.final_project.JavaClasses;

// Class to hold pizza attributes
public class PizzaAttributes {
    public byte[] image;
    public int price;
    public String duration;
    public String ingredients;

    public PizzaAttributes(byte[] image, int price, String duration, String ingredients) {
        this.image = image;
        this.price = price;
        this.duration = duration;
        this.ingredients = ingredients;
    }
}
