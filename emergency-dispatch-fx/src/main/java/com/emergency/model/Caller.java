package com.emergency.model;

public class Caller {
    private int callerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public Caller(int callerId, String firstName, String lastName, String phoneNumber) {
        this.callerId = callerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
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

    
}