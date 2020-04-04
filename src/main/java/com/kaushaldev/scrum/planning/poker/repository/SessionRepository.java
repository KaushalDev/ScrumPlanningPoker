package com.kaushaldev.scrum.planning.poker.repository;

import com.kaushaldev.scrum.planning.poker.model.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        validateSessionId(sessionId);

        sessions.get(sessionId).addUser(user);

        return sessions.get(sessionId);
    }

    public Session addVote(final String sessionId, final String user, final int vote) {
        validateSessionId(sessionId);

        sessions.get(sessionId).addVote(user, vote);

        return sessions.get(sessionId);
    }

    public Session resetSession(final String sessionId) {
        validateSessionId(sessionId);

        sessions.get(sessionId).purgeUsers();

        return sessions.get(sessionId);
    }

    public Session resetVotes(final String sessionId) {
        validateSessionId(sessionId);

        Set<String> users = sessions.get(sessionId)
                                    .getUserVoteMap()
                                    .keySet();

        users.forEach(u -> sessions.get(sessionId).addVote(u, -1));

        return sessions.get(sessionId);
    }

    /**
     * Clean up 1 day old sessions.
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void cleanup() {
        long maxSessionAgenDays = 1;
        List<String> sessionsToDelete = this.sessions
                .values()
                .stream()
                .filter(s -> s.getCreationDate()
                              .plusDays(maxSessionAgenDays)
                              .isBefore(ZonedDateTime.now()))
                .map( s -> s.getSessionId())
                .collect(Collectors.toList());

        sessionsToDelete.forEach(s -> this.sessions.remove(s));
    }

    private boolean validateSessionId(final String sessionId) {
        if (!this.sessions.containsKey(sessionId)) {
            throw new RuntimeException("Invalid session Id");
        }
        return true;
    }
}
