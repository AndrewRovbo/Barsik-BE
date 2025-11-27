package com.barsik.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.barsik.backend.entity.Sitter;

@Repository
public interface SitterRepository extends JpaRepository<Sitter, Long>, JpaSpecificationExecutor<Sitter>{
    boolean existsByUserId(Long userId);
    //void deleteById(Long userId);
}
