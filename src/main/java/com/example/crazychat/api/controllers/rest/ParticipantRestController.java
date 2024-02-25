package com.example.crazychat.api.controllers.rest;

import com.example.crazychat.api.dto.ParticipantDto;
import com.example.crazychat.api.factories.ParticipantDtoFactory;
import com.example.crazychat.api.services.ParticipantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ParticipantRestController {

    ParticipantService participantService;

    ParticipantDtoFactory participantDtoFactory;

    public static final String FETCH_PARTICIPANTS = "/api/chats/{chat_id}/participants";

    @GetMapping(value = FETCH_PARTICIPANTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParticipantDto> fetchParticipants(@PathVariable("chat_id") String chatId) {
        return participantService
                .getParticipants(chatId)
                .map(participantDtoFactory::makeParticipantDto)
                .toList();
    }
}
