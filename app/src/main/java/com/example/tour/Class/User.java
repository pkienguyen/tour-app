package com.example.tour.Class;

public class User {
    private String id;
    private String email;
    private String role;
    private String name;
    private int numberOfBookings;
    private String phone;

    public User() {
    }

    public User(String id, String email, String role, String name, int numberOfBookings, String phone) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.numberOfBookings = numberOfBookings;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfBookings() {
        return numberOfBookings;
    }

    public void setNumberOfBookings(int numberOfBookings) {
        this.numberOfBookings = numberOfBookings;
    }
}
