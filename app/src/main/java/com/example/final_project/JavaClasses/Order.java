package com.example.final_project.JavaClasses;

public class Order {
    private String userEmail;
    private String name;
    private String size;
    private int quantity;
    private double price;
    private String date;
    private String time;
    private byte[] imageData; // Byte array for the image data

    public Order(String userEmail, String name, String size, int quantity, double price, String date, String time, byte[] imageData) {
        this.userEmail = userEmail;
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.time = time;
        this.imageData = imageData;
    }

    // Getters and setters
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
}
