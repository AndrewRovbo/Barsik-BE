package com.barsik.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.barsik.backend.api.DTO.ChatMessageDTO;
import com.barsik.backend.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    @Query("SELECT new com.barsik.backend.api.DTO.ChatMessageDTO(m.chatId, m.senderId, m.recipientId, m.content, m.type, m.timestamp) " +
        "FROM ChatMessage m " +
        "WHERE m.chatId = :chatId ORDER BY m.timestamp ASC")
    List<ChatMessageDTO> findChatMessagesWithUserIds(@Param("chatId") Long chatId);

    @Query("SELECT new com.barsik.backend.api.DTO.ChatMessageDTO(m.id, s.id, r.id, m.content, m.type, m.timestamp) " +
       "FROM ChatMessage m " +
       "JOIN User s ON m.senderId = s.id " +
       "JOIN User r ON m.recipientId = r.id " +
       "WHERE m.chatId = :chatId ORDER BY m.timestamp ASC")
    List<ChatMessageDTO> findChatMessagesWithUserNames(@Param("chatId") Long chatId);

    Page<ChatMessage> findByChatIdOrderByTimestampDesc(Long chatId, Pageable pageable);
}


///теперь как организовать чат между 2 людьми как контроллер для этого. 