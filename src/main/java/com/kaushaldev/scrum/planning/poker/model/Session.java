package com.kaushaldev.scrum.planning.poker.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private final Map<String, UserVote> userVoteMap = new ConcurrentHashMap<>();

    private final int sessionId;

    public Session(int sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, UserVote> getUserVoteMap() {
        return userVoteMap;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void addUser(final String user) {
        userVoteMap.put(user, new UserVote(new User(user),-1));
    }

    public void addVote(final String user, final int vote) {
        userVoteMap.put(user,  new UserVote(new User(user),vote));
    }

    public void purgeUsers() {
        userVoteMap.clear();
    }
}
