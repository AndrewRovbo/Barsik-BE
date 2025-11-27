package com.barsik.backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.barsik.backend.api.DTO.ChatMessageDTO;
import com.barsik.backend.service.ChatMessageService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageDTO>> getChatHistory(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ChatMessageDTO> messages = chatMessageService.getMessagesByChatId(chatId, PageRequest.of(page, size));
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/{userId}")
    public String getAllChatsByUserId(@RequestParam String param) {
        return new String();
    }
    
}
