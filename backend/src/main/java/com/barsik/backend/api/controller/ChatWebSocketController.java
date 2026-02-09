package com.barsik.backend.api.controller;


import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
        // 2. БИЗНЕС-ЛОГИКА: Сохраняем в БД
        // Этот метод должен вернуть DTO с уже проставленным ID чата (если создался новый) и временем
        ChatMessageDTO saved = chatMessageService.sendMessageAndCreateChatIfNotExist(chatMessage);
        
        // 3. ОТПРАВКА: Шлем сообщение в топик конкретного чата
        // Подписчики (и отправитель, и получатель) слушают /topic/chat.{id}
        messagingTemplate.convertAndSend(
            "/queue/messages" + saved.getChatId(),//  "/topic/chat." + saved.getChatId(), 
            saved
        );
        // ОПЦИОНАЛЬНО: Если вы хотите делать глобальные уведомления ("У вас новое сообщение"),
        // можно дополнительно отправить уведомление в личку получателю,
        // но само сообщение чата лучше слать в топик чата.
    }
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
    }
}
