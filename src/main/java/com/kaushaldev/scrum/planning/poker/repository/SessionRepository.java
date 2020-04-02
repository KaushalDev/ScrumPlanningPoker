package com.kaushaldev.scrum.planning.poker.repository;

import com.kaushaldev.scrum.planning.poker.model.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Scope(value = "singleton")
public class SessionRepository {
    private Map<String, Session> sessions;

    public SessionRepository() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public Session createSession(final String user) {
        String sessionId = UUID.randomUUID().toString();

        sessions.putIfAbsent(sessionId, new Session(sessionId));
        sessions.get(sessionId).addUser(user);

        return sessions.get(sessionId);
    }

    public Session addUserInSession(final String sessionId, final String user) {
        sessions.get(sessionId).addUser(user);

        return sessions.get(sessionId);
    }

    public Session addVote(final String sessionId, final String user, final int vote) {
        sessions.get(sessionId).addVote(user, vote);

        return sessions.get(sessionId);
    }

    public Session resetSession(final String sessionId) {
        sessions.get(sessionId).purgeUsers();

        return sessions.get(sessionId);
    }

    public Session resetVotes(final String sessionId) {
        Set<String> users = sessions.get(sessionId)
                                    .getUserVoteMap()
                                    .keySet();

        users.forEach(u -> sessions.get(sessionId).addVote(u, -1));

        return sessions.get(sessionId);
    }
}
