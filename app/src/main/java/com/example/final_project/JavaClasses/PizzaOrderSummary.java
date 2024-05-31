package com.example.final_project.JavaClasses;

public class PizzaOrderSummary {
    private String pizzaName;
    private int orderQuantity;
    private double totalIncome;
    private String orderDate;
    private String orderTime;

    public PizzaOrderSummary(String pizzaName, int orderQuantity, double totalIncome, String orderDate, String orderTime) {
        this.pizzaName = pizzaName;
        this.orderQuantity = orderQuantity;
        this.totalIncome = totalIncome;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }
}

