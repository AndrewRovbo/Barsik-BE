package com.barsik.backend.api.DTO;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatDTO {

    private Long chatId;
    private String name;
    private List<Long> participantUserIds;
    private List<String> participantUsernames;
    private Instant lastMessageTime;
    private int unreadCount;
}
