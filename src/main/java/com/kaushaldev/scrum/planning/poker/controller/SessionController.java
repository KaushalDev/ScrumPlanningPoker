package com.kaushaldev.scrum.planning.poker.controller;

import com.kaushaldev.scrum.planning.poker.model.Session;
import com.kaushaldev.scrum.planning.poker.repository.SessionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    private SessionRepository sessionRepository;

    public SessionController(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/sessions")
    public Session create(@RequestParam(value = "user", required = false) String user) {
        return sessionRepository.createSession(user);
    }
}
