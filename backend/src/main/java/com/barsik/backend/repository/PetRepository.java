package com.barsik.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.barsik.backend.entity.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>{
    //Optional<Pet> findByOwnerUserIdAndSlug(Long ownerId, String slug);
    @Query("SELECT p FROM Pet p WHERE p.owner.id = :ownerId")
    List<Pet> findByOwnerIdPets(@Param("ownerId") Long ownerId);
    
}
