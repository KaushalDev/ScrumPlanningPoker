package com.kaushaldev.scrum.planning.poker.controller;

import com.kaushaldev.scrum.planning.poker.model.Session;
import com.kaushaldev.scrum.planning.poker.repository.SessionRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class SessionController {
    private SessionRepository sessionRepository;
    private SimpMessagingTemplate messagingTemplate;

    public SessionController(final SessionRepository sessionRepository,
                             final SimpMessagingTemplate messagingTemplate) {
        this.sessionRepository = sessionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/sessions")
    public Collection<Session> get() {
        return sessionRepository.getSessions();
    }

    @PostMapping("/sessions")
    public Session create(@RequestParam(value = "user", required = false) String user) {
        return sessionRepository.createSession(user);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public void delete(@PathVariable(value = "sessionId") String sessionId) {
        sessionRepository.deleteSession(sessionId);
        messagingTemplate.convertAndSend("/topic/sessions/" + sessionId + "/closed", "");
    }

    @DeleteMapping("/sessions/{sessionId}/users/{user}")
    public void delete(@PathVariable(value = "sessionId") String sessionId,
                       @PathVariable(value = "user") String user) {
        final Session session = sessionRepository.removeUser(sessionId, user);
        messagingTemplate.convertAndSend("/topic/sessions/" + sessionId, session.getVotesSummary());
    }
}
