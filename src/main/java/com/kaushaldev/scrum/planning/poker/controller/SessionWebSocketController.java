package com.kaushaldev.scrum.planning.poker.controller;

import com.kaushaldev.scrum.planning.poker.model.Session;
import com.kaushaldev.scrum.planning.poker.model.User;
import com.kaushaldev.scrum.planning.poker.model.UserVote;
import com.kaushaldev.scrum.planning.poker.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SessionWebSocketController {
    private SessionRepository sessionRepository;

    private SimpMessagingTemplate messagingTemplate;

    public SessionWebSocketController(final SessionRepository sessionRepository,
                                      final SimpMessagingTemplate messagingTemplate) {
        this.sessionRepository = sessionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sessions/{sessionId}/users")
    public void addUser(@DestinationVariable int sessionId,
                        final User user) {
        final Session session = sessionRepository.addUserInSession(sessionId, user.getUsername());

        sendMessageToSession(sessionId, session);
    }

    @MessageMapping("/sessions/{sessionId}/vote")
    public void addVote(@DestinationVariable int sessionId,
                           final UserVote vote) {
        final Session session = sessionRepository.addVote(sessionId,
                vote.getUser().getUsername(),
                vote.getVote());

        sendMessageToSession(sessionId, session);
    }

    @MessageMapping("/sessions/{sessionId}/reset")
    public void reset(@DestinationVariable int sessionId) {
        final Session session =  sessionRepository.resetVotes(sessionId);

        sendMessageToSession(sessionId, session);
    }

    @MessageMapping("/sessions/{sessionId}/purge")
    @SendTo("/sessions/{sessionId}")
    public void purgeUsers(@DestinationVariable int sessionId) {
        final Session session =  sessionRepository.resetSession(sessionId);

        sendMessageToSession(sessionId, session);
    }

    private void sendMessageToSession(final int sessionId,
                                      final Session session) {
        messagingTemplate.convertAndSend("/topic/sessions/" + sessionId, session);
    }
}
