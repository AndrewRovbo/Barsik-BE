package com.barsik.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.PetRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Pet;
import com.barsik.backend.repository.PetRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class PetService {

    @Autowired private PetRepository petRepository;
    //@Autowired private OwnerRepository ownerRepository;

    private String buildPetSlug(Pet pet) {
    return Stream.of(pet.getName(), pet.getType(), pet.getBreed(), pet.getGender(), String.valueOf(pet.getAge()))
        .map(val -> val == null ? "" : val.trim().toLowerCase().replaceAll("[^a-z0-9]", ""))
        .collect(Collectors.joining("-"));
    };

    public Pet findPetBySlug(Long ownerId, String slug) {
        List<Pet> pets = petRepository.findByOwnerIdPets(ownerId);
        return pets.stream()
            .filter(pet -> buildPetSlug(pet).equals(slug))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Питомец не найден или нет доступа"));
    }

    public List<Pet> getAllPetsByOwner(Long ownerId){
        return petRepository.findByOwnerIdPets(ownerId);
    }


    public Pet savePet(Owner owner, PetRequest savePet) {//dto!!
        Pet pet = new Pet();

        pet.setName(savePet.getName());
        pet.setType(savePet.getType());
        pet.setBreed(savePet.getBreed());
        pet.setAge(savePet.getAge());
        pet.setGender(savePet.getGender());
        pet.setDescription(savePet.getDescription());
        pet.setPhotoUrl(savePet.getPhotoUrl());
        pet.setOwner(owner);


        return petRepository.save(pet);
    }

    public void deletePetByOwner(Long ownerId, String slug){
        Pet deletePet = findPetBySlug(ownerId, slug);
        petRepository.deleteById(deletePet.getId());
    };

//delete for admin

    public void updatePetByOwner(Long ownerId, String slug, PetRequest petUpdate){
        Pet pet = findPetBySlug(ownerId, slug);

        if (petUpdate.getName() != null) pet.setName(petUpdate.getName());
        if (petUpdate.getType() != null) pet.setType(petUpdate.getType());
        if (petUpdate.getBreed() != null) pet.setBreed(petUpdate.getBreed());
        if (petUpdate.getAge() != null) pet.setAge(petUpdate.getAge());
        if (petUpdate.getGender() != null) pet.setGender(petUpdate.getGender());
        if (petUpdate.getDescription() != null) pet.setDescription(petUpdate.getDescription());
        if (petUpdate.getPhotoUrl() != null) pet.setPhotoUrl(petUpdate.getPhotoUrl());
        petRepository.save(pet);
    };
    
    //public void getPet(){};



    
}