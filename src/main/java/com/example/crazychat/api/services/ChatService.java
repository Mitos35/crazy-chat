package com.example.crazychat.api.services;

import com.example.crazychat.api.domains.Chat;
import com.example.crazychat.api.factories.ChatDtoFactory;
import com.example.crazychat.api.controllers.ws.ChatWsController;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ChatService {
    ChatDtoFactory chatDtoFactory;

    SimpMessagingTemplate messagingTemplate;


    SetOperations<String, Chat> setOperations;

    private static final String KEY = "may:code:crazy-chat:chats";

    public void createChat(String chatName) {

        log.info(String.format("Chat \"%s\" created.", chatName));

        Chat chat = Chat.builder()
                .name(chatName)
                .build();

        setOperations.add(KEY, chat);

        messagingTemplate.convertAndSend(
                ChatWsController.FETCH_CREATE_CHAT_EVENT,
                chatDtoFactory.makeChatDto(chat)
        );
    }

    public void deleteChat(String chatId) {

        getChats()
                .filter(chat -> Objects.equals(chatId, chat.getId()))
                .findAny()
                .ifPresent(chat -> {

                    log.info(String.format("Chat \"%s\" deleted.", chat.getName()));

                    setOperations.remove(KEY, chat);

                    messagingTemplate.convertAndSend(
                            ChatWsController.FETCH_DELETE_CHAT_EVENT,
                            chatDtoFactory.makeChatDto(chat)
                    );
                });
    }


    public Stream<Chat> getChats() {
        return Optional
                .ofNullable(setOperations.members(KEY))
                .orElseGet(HashSet::new)
                .stream();
    }
}
