package com.example.tour.Class;

public class Payment {
    private int id;
    private int totalPrice;
    private String date;
    private String email;
    private String method;

    public Payment() {
    }

    public Payment(int id, int totalPrice, String date, String email, String method) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.date = date;
        this.email = email;
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
