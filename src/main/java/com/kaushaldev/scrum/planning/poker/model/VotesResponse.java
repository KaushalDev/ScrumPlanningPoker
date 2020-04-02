package com.kaushaldev.scrum.planning.poker.model;

public class VotesResponse {
    public final static String VOTED = "Voted";
    public final static String WAITING_VOTE = "Awaiting vote";

    private String user;

    private String voteState;

    public VotesResponse() {
    }

    public VotesResponse(String user, String voteState) {
        this.user = user;
        this.voteState = voteState;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVoteState() {
        return voteState;
    }

    public void setVoteState(String voteState) {
        this.voteState = voteState;
    }
}
