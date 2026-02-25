package com.barsik.backend.api.controller;


import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import com.barsik.backend.api.DTO.ChatMessageDTO;
import com.barsik.backend.security.CustomUserDetails;
import com.barsik.backend.service.ChatMessageService;


@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessage, Principal principal) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long senderId = userDetails.getUserId();
        
        chatMessage.setSenderId(senderId);
        
        ChatMessageDTO saved = chatMessageService.sendMessageAndCreateChatIfNotExist(chatMessage);
        
        messagingTemplate.convertAndSendToUser(
            String.valueOf(saved.getRecepientId()),
            "/queue/messages",
            saved
        );

        // Если у отправителя открыто несколько вкладок браузера, чтобы сообщение появилось везде:
        messagingTemplate.convertAndSendToUser(
            String.valueOf(senderId),
            "/queue/messages",
            saved
        );
    }
    /*
    @MessageMapping("/chat.confirmReceived")
    public void confirmReceived(@Payload Long messageId) {
        chatMessageService.markReceived(messageId);
    }

    @MessageMapping("/chat.confirmDelivered")
    public void confirmDelivered(@Payload Long messageId) {
        chatMessageService.markDelivered(messageId);
    }

    @MessageMapping("/chat.confirmRead")
    public void confirmRead(@Payload Long messageId) {
        chatMessageService.markRead(messageId);
        ChatMessageDTO messageDto = chatMessageService.getMessage(messageId);

        // Уведомляем отправителя, что сообщение прочитано
        messagingTemplate.convertAndSendToUser(
            messageDto.getSenderId().toString(),
            "/queue/read",
            messageDto
        );
    }
    @MessageMapping("/chat.broadcast")
    //@PreAuthorize("hasRole('ADMIN')")
    public void broadcast(@Payload ChatMessageDTO message) {
        //message.setType(MessageType.SYSTEM);
        messagingTemplate.convertAndSend("/topic/global", message);
    }

    @MessageMapping("/chat.broadcastToOnline")
    @SendTo("/topic/onlineUsers")
    //@PreAuthorize("hasRole('ADMIN')")
    public ChatMessageDTO broadcastToOnline(ChatMessageDTO message) {
        return message;
    }*/
}
