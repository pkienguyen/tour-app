package com.example.tour.Class;

import java.io.Serializable;

public class Ticket implements Serializable {
    private int id;
    private int tourId;
    private String title;
    private int totalPrice;
    private int persons;
    private String visitDate;
    private String email;
    private String date;
    private int status;
    private String duration;
    private int paymentId;
    private String note;

    public Ticket() {
    }

    public Ticket(int id, String title, int totalPrice, int persons, String visitDate, String email, String date, int status, String duration, int paymentId) {
        this.id = id;
        this.title = title;
        this.totalPrice = totalPrice;
        this.persons = persons;
        this.visitDate = visitDate;
        this.email = email;
        this.date = date;
        this.status = status;
        this.duration = duration;
        this.paymentId = paymentId;
    }

    public Ticket(int tourId, String title, int totalPrice, int persons, String visitDate, String duration) {
        this.tourId = tourId;
        this.title = title;
        this.totalPrice = totalPrice;
        this.persons = persons;
        this.visitDate = visitDate;
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
