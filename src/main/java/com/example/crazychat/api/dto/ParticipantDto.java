package com.example.crazychat.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantDto {

    String id;

    @Builder.Default
    Instant enterAt = Instant.now();
}
