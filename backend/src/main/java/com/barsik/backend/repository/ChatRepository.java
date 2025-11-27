package com.barsik.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.barsik.backend.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>{
    @Query("SELECT c FROM Chat c WHERE (c.participant1Id = :senderId AND c.participant2Id = :recipientId) OR (c.participant1Id = :recipientId AND c.participant2Id = :senderId)")
    Optional<Chat> findByParticipants(@Param("senderId") Long id1, @Param("recipientId") Long id2);

}
