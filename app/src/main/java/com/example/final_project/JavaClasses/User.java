package com.example.final_project.JavaClasses;

public class User {
    private String name;
    private String username;
    private String email;
    private String phone;
    private String gender;
    private String password;
    private int role;

    public User(String name, String username, String email, String phone, String gender, String password, int role) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.password = password;
        this.role = role;
    }

    // Getters and setters...

    public int getRole() {
        return role;
    }
}
