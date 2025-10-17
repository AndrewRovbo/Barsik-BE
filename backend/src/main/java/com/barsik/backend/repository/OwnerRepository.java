package com.barsik.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barsik.backend.entity.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long>{
    boolean existsByUserId(Long userId);
}
