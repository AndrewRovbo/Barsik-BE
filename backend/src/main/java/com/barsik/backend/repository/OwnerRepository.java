package com.barsik.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barsik.backend.entity.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long>{
    
}
