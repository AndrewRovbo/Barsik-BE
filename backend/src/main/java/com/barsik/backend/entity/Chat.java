package com.barsik.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chats"
, uniqueConstraints = @UniqueConstraint(columnNames = {"participant1_id", "participant2_id"}))
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "participant1_id",nullable = false) 
    private Long participant1Id;
    @Column(name = "participant2_id",nullable = false)
    private Long participant2Id;

    @Column(updatable = false, name = "created_at",nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;
}
