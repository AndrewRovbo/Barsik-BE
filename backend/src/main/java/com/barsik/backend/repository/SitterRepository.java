package com.barsik.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barsik.backend.entity.Sitter;

@Repository
public interface SitterRepository extends JpaRepository<Sitter, Long>{
    boolean existsByUserId(Long userId);
}
