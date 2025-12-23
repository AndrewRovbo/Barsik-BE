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
        Long senderId = messageDto.getSenderId();
        Long recipientId = messageDto.getRecipientId();

        // Используем симметричный поиск
        Chat chat = chatRepository.findByParticipants(senderId, recipientId)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setParticipant1Id(Math.min(senderId, recipientId));
                    newChat.setParticipant2Id(Math.max(senderId, recipientId));
                    newChat.setCreatedAt(LocalDateTime.now());
                    newChat.setUpdatedAt(LocalDateTime.now());
                    return chatRepository.save(newChat);
                });

        ChatMessage message = new ChatMessage();
        message.setChatId(chat.getId());
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(messageDto.getContent());
        message.setType(messageDto.getType());
        message.setStatus(MessageStatus.SENDING);
        message.setTimestamp(LocalDateTime.now());

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
