package com.example.crazychat.api.controllers.rest;

import com.example.crazychat.api.dto.ChatDto;
import com.example.crazychat.api.factories.ChatDtoFactory;
import com.example.crazychat.api.services.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ChatRestController {

    ChatService chatService;

    ChatDtoFactory chatDtoFactory;

    public static final String FETCH_CHATS = "/api/chats";

    @GetMapping(value = FETCH_CHATS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatDto> fetchChats() {
        return chatService
                .getChats()
                .map(chatDtoFactory::makeChatDto)
                .toList();
    }
}
