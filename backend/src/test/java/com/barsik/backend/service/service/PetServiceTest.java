package com.barsik.backend.service.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barsik.backend.api.DTO.request.PetRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Pet;
import com.barsik.backend.repository.PetRepository;
import com.barsik.backend.service.PetService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void findPetBySlug_ShouldReturnPetWhenSlugMatches() {
        Long ownerId = 1L;

        Pet pet = new Pet();
        pet.setId(10L);
        pet.setName("Buddy");
        pet.setType("Dog");
        pet.setBreed("Pug");
        pet.setGender("Male");
        pet.setAge((byte) 5);
        String expectedSlug = "buddy-dog-pug-male-5";

        Pet otherPet = new Pet();
        otherPet.setName("Kitty");
        otherPet.setType("Cat");
        otherPet.setBreed("Persian");
        when(petRepository.findByOwnerIdPets(ownerId)).thenReturn(List.of(otherPet, pet));

        Pet foundPet = petService.findPetBySlug(ownerId, expectedSlug);


        assertNotNull(foundPet);
        assertEquals("Buddy", foundPet.getName());
    }

    @Test
    void findPetBySlug_ShouldThrowWhenNoMatch() {
        Long ownerId = 1L;
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setType("Dog");
        pet.setBreed("Pug");
        pet.setGender("Male");      
        String wrongSlug = "wrong-slug-123";

        when(petRepository.findByOwnerIdPets(ownerId)).thenReturn(List.of(pet));

        assertThrows(EntityNotFoundException.class, 
            () -> petService.findPetBySlug(ownerId, wrongSlug));
        }


    @Test
    void savePet_ShouldMapFieldsAndSave() {
        Owner owner = new Owner();
        owner.setUserId(1L);

        PetRequest request = new PetRequest();
        request.setName("Rex");
        request.setType("Dog");
        request.setAge((byte)3);


        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pet savedPet = petService.savePet(owner, request);


        assertEquals(savedPet.getName(), "Rex");
        assertEquals(savedPet.getType(),"Dog");
        assertEquals(savedPet.getAge(), (byte)3);
        assertEquals(savedPet.getOwner(), owner);
        
        verify(petRepository, times(1)).save(any(Pet.class));
    }



    @Test
    void deletePetByOwner_ShouldFindAndDelete() {

        Long ownerId = 1L;
        String slug = "rex-dog-shepherd-male-3";
        Long petId = 55L;

        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Rex");
        pet.setType("Dog");
        pet.setBreed("Shepherd");
        pet.setGender("Male");
        pet.setAge((byte)3);


        when(petRepository.findByOwnerIdPets(ownerId)).thenReturn(List.of(pet));

        petService.deletePetByOwner(ownerId, slug);


        verify(petRepository).deleteById(petId);
    }


    @Test
    void updatePetByOwner_ShouldUpdateOnlyNotNullFields() {

        Long ownerId = 1L;
        String slug = "old-slug-params";
        
        Pet existingPet = new Pet();
        existingPet.setName("OldName");
        existingPet.setType("OldType");
        existingPet.setAge((byte)5);
        
        PetRequest updateRequest = new PetRequest();
        updateRequest.setName("NewName"); 
        updateRequest.setType(null); 
        updateRequest.setAge(null); 

        when(petRepository.findByOwnerIdPets(ownerId)).thenReturn(List.of(existingPet));

        existingPet.setBreed("Breed"); existingPet.setGender("M");
        String matchingSlug = "oldname-oldtype-breed-m-5"; 
        

        petService.updatePetByOwner(ownerId, matchingSlug, updateRequest);

        
        assertEquals(existingPet.getName(), "NewName");
        assertEquals(existingPet.getType(),"OldType");
        assertEquals(existingPet.getAge(), (byte)5);

        verify(petRepository).save(existingPet);
    }
}
