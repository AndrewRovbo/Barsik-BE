package com.barsik.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barsik.backend.entity.Sitter;

public interface SitterRepository extends JpaRepository<Sitter, Long>{
    
}
