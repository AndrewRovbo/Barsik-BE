package com.barsik.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barsik.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
