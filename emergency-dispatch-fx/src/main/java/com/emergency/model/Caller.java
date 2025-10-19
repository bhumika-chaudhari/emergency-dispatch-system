package com.emergency.model;

public class Caller {
    private int callerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    // --- Constructor ---
    public Caller(int callerId, String firstName, String lastName, String phoneNumber, String email) {
        this.callerId = callerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // --- Getters ---
    public int getCallerId() {
        return callerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    // --- Setters ---
    public void setCallerId(int callerId) {
        this.callerId = callerId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}