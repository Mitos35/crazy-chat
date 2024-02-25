package com.example.crazychat.api.factories;

import com.example.crazychat.api.domains.Chat;
import com.example.crazychat.api.dto.ChatDto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ChatDtoFactory {

    public ChatDto makeChatDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .createdAt(Instant.ofEpochMilli(chat.getCreatedAt()))
                .build();
    }
}
