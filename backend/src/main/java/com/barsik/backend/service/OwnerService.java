package com.barsik.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.PetRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Pet;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.OwnerRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


@Service
public class OwnerService {

    @Autowired private OwnerRepository ownerRepository;
    @Autowired private PetService petService;

    @Transactional
    public void updateOwnerProfile(Long userId, OwnerProfileUpdateRequest request) {
        Owner owner = ownerRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Owner profile not found"));

        if (request.getAboutMe() != null) {
            owner.setAboutMe(request.getAboutMe());
        }

        ownerRepository.save(owner);
        //userProfileService.updateTimestamp(userId);
    }

    @Transactional
    public void deleteOwner(Long userId) {
        Owner owner = ownerRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        User user = owner.getUser();
        user.setOwner(null);
        ownerRepository.delete(owner);
    }

    public boolean existsByUserId(Long userId) {
        return ownerRepository.existsById(userId);
    }

    public Owner getByUserId(Long userId) {
        return ownerRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Owner not found"));
    }

    public List<Pet> getAllPets(Long ownerId){
        return petService.getAllPetsByOwner(ownerId);
    }
    public void addPet(Long ownerId, PetRequest pet){
        Owner owner = getByUserId(ownerId);
        petService.savePet(owner, pet);        
    };

    public void updatePet(Long ownerId, String slug, PetRequest petUpdate){
        petService.updatePetByOwner(ownerId, slug, petUpdate);
    };
    public void deletePet(Long ownerId, String slug){
        petService.deletePetByOwner(ownerId, slug);
    };
    
}
