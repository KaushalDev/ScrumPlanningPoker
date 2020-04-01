package com.kaushaldev.scrum.planning.poker.repository;

import com.kaushaldev.scrum.planning.poker.model.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Scope(value = "singleton")
public class SessionRepository {
    private Map<Integer, Session> sessions;
    private AtomicInteger nextSessionId;

    public SessionRepository() {
        this.sessions = new ConcurrentHashMap<>();
        nextSessionId = new AtomicInteger();
        nextSessionId.set(1);
    }

    public Session createSession(final String user) {
        int sessionId = nextSessionId.getAndIncrement();

        sessions.putIfAbsent(sessionId, new Session(sessionId));
        sessions.get(sessionId).addUser(user);

        return sessions.get(sessionId);
    }

    public Session addUserInSession(final int sessionId, final String user) {
        sessions.get(sessionId).addUser(user);

        return sessions.get(sessionId);
    }

    public Session addVote(final int sessionId, final String user, final int vote) {
        sessions.get(sessionId).addVote(user, vote);

        return sessions.get(sessionId);
    }

    public Session resetSession(final int sessionId) {
        sessions.get(sessionId).purgeUsers();

        return sessions.get(sessionId);
    }

    public Session resetVotes(final int sessionId) {
        Set<String> users = sessions.get(sessionId)
                                    .getUserVoteMap()
                                    .keySet();

        users.forEach(u -> sessions.get(sessionId).addVote(u, -1));

        return sessions.get(sessionId);
    }
}
