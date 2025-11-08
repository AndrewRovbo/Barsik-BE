package com.barsik.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "messages")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @Column(name = "chat_id")
   private Long chatId;
   
   @Column(name = "sender_id")
   private Long senderId;
   @Column(name = "recipient_id")
   private Long recipientId;
   @Column(name = "sender_name")
   private String senderName;
   @Column(name = "recipient_name")
   private String recipientName;
   @Column(nullable = false)
   private String content;
   @Column(nullable = false)
   private LocalDateTime timestamp = LocalDateTime.now();
   
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private MessageStatus status;
   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private MessageType type;
}
