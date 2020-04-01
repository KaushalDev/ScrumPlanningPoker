package com.kaushaldev.scrum.planning.poker.model;

public class User {
    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
