package com.example.crazychat.api.controllers.ws;

import com.example.crazychat.api.dto.ParticipantDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantWsController {

    public static final String FETCH_PARTICIPANT_JOIN_IN_CHAT = "/topic/chats.{chat_id}.participants.join";
    public static final String FETCH_PARTICIPANT_LEAVE_FROM_CHAT = "/topic/chats.{chat_id}.participants.leave";

    @SubscribeMapping(FETCH_PARTICIPANT_JOIN_IN_CHAT)
    public ParticipantDto fetchParticipantJoinInChat() {
        return null;
    }

    @SubscribeMapping(FETCH_PARTICIPANT_LEAVE_FROM_CHAT)
    public ParticipantDto fetchParticipantLeaveFromChat() {
        return null;
    }

    public static String getFetchParticipantJoinInChatDestination(String chatId) {
        return FETCH_PARTICIPANT_JOIN_IN_CHAT.replace("{chat_id}", chatId);
    }

    public static String getFetchParticipantLeaveFromChatDestination(String chatId) {
        return FETCH_PARTICIPANT_LEAVE_FROM_CHAT.replace("{chat_id}", chatId);
    }
}
