package com.barsik.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.ChatMessageDTO;
import com.barsik.backend.entity.Chat;
import com.barsik.backend.entity.ChatMessage;
import com.barsik.backend.entity.MessageStatus;
import com.barsik.backend.entity.MessageType;
import com.barsik.backend.repository.ChatMessageRepository;
import com.barsik.backend.repository.ChatRepository;

import jakarta.persistence.EntityNotFoundException;



@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired private ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatHistory(Long chatId) {
        return chatMessageRepository.findChatMessagesWithUserIds(chatId);
    }

    @Transactional
    public ChatMessageDTO sendMessageAndCreateChatIfNotExist(ChatMessageDTO messageDto) {
        // Находим чат, если он существует
        Chat chat = chatRepository.findByParticipants(messageDto.getSenderId(), messageDto.getRecipientId())
                .orElseGet(() -> {
                    // Если чат не найден, создаем новый
                    Chat newChat = new Chat();
                    newChat.setParticipant1Id(messageDto.getSenderId());
                    newChat.setParticipant2Id(messageDto.getRecipientId());
                    newChat.setCreatedAt(LocalDateTime.now());  // Устанавливаем дату создания чата
                    messageDto.setType(MessageType.JOIN);  // Этот тип сообщения можно использовать для уведомления о создании чата
                    return chatRepository.save(newChat);
                });

        // Создаем новое сообщение
        ChatMessage message = new ChatMessage();
        message.setChatId(chat.getId());
        message.setSenderId(messageDto.getSenderId());
        message.setRecipientId(messageDto.getRecipientId());
        message.setContent(messageDto.getContent());
        message.setType(messageDto.getType());
        message.setStatus(MessageStatus.SENDING);
        message.setTimestamp(LocalDateTime.now());  // Устанавливаем время отправки сообщения

        // Сохраняем сообщение в базе данных
        return toDTO(chatMessageRepository.save(message));
    }
    // Сообщение отправлено клиентом
    @Transactional
    public void markReceived(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        message.setStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(message);
    }

    // Сообщение доставлено устройству получателя
    @Transactional
    public void markDelivered(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        message.setStatus(MessageStatus.DELIVERED);
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(message);
    }

    // Сообщение прочитано получателем
    @Transactional
    public void markRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        message.setStatus(MessageStatus.READ);
        chatMessageRepository.save(message);
    }


    public ChatMessageDTO getMessage(Long messageId) {
        ChatMessage m = chatMessageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found"));
        return toDTO(m);
    }

    public Page<ChatMessageDTO> getMessagesByChatId(Long chatId, Pageable pageable) {
        return chatMessageRepository.findByChatIdOrderByTimestampDesc(chatId, pageable)
                                .map(this::toDTO);
    }

    private ChatMessage toEntityS(ChatMessageDTO dto) {
        ChatMessage entity = new ChatMessage();
        entity.setChatId(dto.getChatId());
        entity.setSenderId(dto.getSenderId());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setStatus(MessageStatus.SENDING);
        entity.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        return entity;
    }

    private ChatMessageDTO toDTO(ChatMessage entity) {
        ChatMessageDTO dto = new ChatMessageDTO(
            entity.getChatId(),
            entity.getSenderId(), 
            entity.getRecipientId(), 
            entity.getContent(), 
            entity.getType(),
            entity.getTimestamp());
        return dto;
    }
}
