package com.barsik.backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.barsik.backend.api.DTO.ChatDTO;
import com.barsik.backend.api.DTO.ChatMessageDTO;
import com.barsik.backend.service.ChatMessageService;
import com.barsik.backend.service.ChatService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatService chatService;

    // 1️⃣ Получить список всех чатов пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatDTO>> getAllChatsByUserId(@PathVariable Long userId) {
        List<ChatDTO> chats = chatService.getChatsByUserId(userId);
        return ResponseEntity.ok(chats);
    }

    // 2️⃣ Получить историю сообщений в чате
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageDTO>> getChatHistory(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ChatMessageDTO> messages = chatMessageService.getMessagesByChatId(chatId, PageRequest.of(page, size));
        return ResponseEntity.ok(messages);
    }

    // 3️⃣ Отправить сообщение (создает чат, если его нет)
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatMessageDTO messageDto) {
        // messageDto должен содержать senderId, recipientId, content
        ChatMessageDTO savedMessage = chatMessageService.sendMessageAndCreateChatIfNotExist(messageDto);
        return ResponseEntity.ok(savedMessage);
    }
}
