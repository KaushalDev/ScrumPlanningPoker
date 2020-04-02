package com.kaushaldev.scrum.planning.poker.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Session {
    public static final int NOT_VOTED_VALUE = -1;
    private final Map<String, UserVote> userVoteMap = new ConcurrentHashMap<>();

    private final String sessionId;

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, UserVote> getUserVoteMap() {
        return userVoteMap;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void addUser(final String user) {
        userVoteMap.put(user, new UserVote(new User(user), NOT_VOTED_VALUE));
    }

    public void addVote(final String user, final int vote) {
        userVoteMap.put(user,  new UserVote(new User(user),vote));
    }

    public void purgeUsers() {
        userVoteMap.clear();
    }

    public List<VotesResponse> getVotesSummary() {
        final boolean anyVotePending = this.userVoteMap.values().stream().anyMatch(v -> v.getVote() == NOT_VOTED_VALUE);

        return this.userVoteMap.values()
                               .stream()
                               .map(v -> this.mapToVoteResponse(anyVotePending, v))
                               .collect(Collectors.toList());
    }

    private VotesResponse mapToVoteResponse(boolean maskVotes, UserVote v) {
        return new VotesResponse(
                v.getUser().getUsername(),
                maskVotes ? getMaskedVoteValue(v) : String.valueOf(v.getVote()));
    }

    private String getMaskedVoteValue(UserVote v) {
        return v.getVote() == -1
                ? VotesResponse.WAITING_VOTE
                : VotesResponse.VOTED;
    }
}
