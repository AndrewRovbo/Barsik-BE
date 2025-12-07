package com.barsik.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.ChatDTO;
import com.barsik.backend.entity.Chat;
import com.barsik.backend.repository.ChatMessageRepository;
import com.barsik.backend.repository.ChatRepository;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository; // для lastMessageTime и unreadCount

    public List<ChatDTO> getChatsByUserId(Long userId) {
        List<Chat> chats = chatRepository.findChatsByUserId(userId);

        return chats.stream()
                .map(chat -> toDTO(chat, userId))
                .collect(Collectors.toList());
    }

    private ChatDTO toDTO(Chat chat, Long currentUserId) {
        ChatDTO dto = new ChatDTO();
        dto.setChatId(chat.getId());
        dto.setParticipantUserIds(List.of(chat.getParticipant1Id(), chat.getParticipant2Id()));
        // если есть usernames:
        dto.setParticipantUsernames(List.of(
                chat.getParticipant1Id().toString(), // позже заменишь на username
                chat.getParticipant2Id().toString()
        ));
        // последнее сообщение
        dto.setLastMessageTime(chatMessageRepository.findLastMessageTime(chat.getId()).orElse(null));
        // количество непрочитанных сообщений
        dto.setUnreadCount(chatMessageRepository.countUnreadMessages(chat.getId(), currentUserId));
        return dto;
    }
}
