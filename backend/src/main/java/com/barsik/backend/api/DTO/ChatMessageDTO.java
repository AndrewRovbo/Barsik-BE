package com.barsik.backend.api.DTO;

import java.time.LocalDateTime;

import com.barsik.backend.entity.MessageType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private Long chatId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
}