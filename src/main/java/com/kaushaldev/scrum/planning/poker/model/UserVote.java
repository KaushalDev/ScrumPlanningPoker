package com.kaushaldev.scrum.planning.poker.model;

public class UserVote {
    private User user;

    private int vote;

    public UserVote() {
    }

    public UserVote(User user, int vote) {
        this.user = user;
        this.vote = vote;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
