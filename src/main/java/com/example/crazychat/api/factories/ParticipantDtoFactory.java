package com.example.crazychat.api.factories;

import com.example.crazychat.api.domains.Participant;
import com.example.crazychat.api.dto.ParticipantDto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ParticipantDtoFactory {
    public ParticipantDto makeParticipantDto(Participant participant) {
        return ParticipantDto.builder()
                .id(participant.getId())
                .enterAt(Instant.ofEpochMilli(participant.getEnterAt()))
                .build();
    }
}
