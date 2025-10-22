package com.emergency.model;

public class Witness {
    private String name;
    private String phone;
    private String statement;
    private int witnessId;

    public Witness(int witnessId, String name, String phone, String statement) { // <-- ADD ID
        this.witnessId = witnessId;
        this.name = name;
        this.phone = phone;
        this.statement = statement;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public int getWitnessId() {
        return witnessId;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatement() {
        return statement;
    }

    // --- Setters ---
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    // This method is useful for displaying the witness in a ListView
    @Override

    public String toString() {
        // This formats how each witness will look in the list
        return name + " (" + phone + "):\n\t\"" + statement + "\"";
    }
}