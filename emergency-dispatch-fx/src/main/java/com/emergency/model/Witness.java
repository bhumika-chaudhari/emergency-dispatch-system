package com.emergency.model;

public class Witness {
    private String name;
    private String phone;
    private String statement;

    // Constructor to easily create new witness objects
    public Witness(String name, String phone, String statement) {
        this.name = name;
        this.phone = phone;
        this.statement = statement;
    }

    // --- Getters ---
    public String getName() {
        return name;
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