package com.barsik.backend.service.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.api.DTO.request.PetRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Pet;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.OwnerRepository;
import com.barsik.backend.service.OwnerService;
import com.barsik.backend.service.PetService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {
    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetService petService;

    @InjectMocks
    private OwnerService ownerService;

    @BeforeAll
    public static void BeforeAll(){
        MockitoAnnotations.openMocks(OwnerServiceTest.class);
    }

    @Test
    public void getByUserId_ShouldReturnOwnerWhenExists(){

        Long userId = 1L;
        Owner expectedOwner = new Owner();
        expectedOwner.setUserId(userId);
        when(ownerRepository.findById(userId)).thenReturn(Optional.of(expectedOwner));


        Owner actualOwner = ownerService.getByUserId(userId);

        assertNotNull(actualOwner);
        assertEquals(userId, actualOwner.getUserId());
        
        verify(ownerRepository, times(1)).findById(userId);
    }
    @Test
    public void getByUserId_ShouldThrowExceptionWhenNotFound(){
        Long userId = 99L;
        when(ownerRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ownerService.getByUserId(userId));

        verify(ownerRepository, times(1)).findById(userId);
    }
    @Test 
    public void updateOwnerProfile_ShouldUpdateFields(){
        Long userId = 1L;
        String aboutMe = "I want money";
        OwnerProfileUpdateRequest r = new OwnerProfileUpdateRequest();
        r.setAboutMe(aboutMe);

        Owner existOwner = new Owner();
        existOwner.setUserId(userId);
        existOwner.setAboutMe("old data");

        when(ownerRepository.findById(userId)).thenReturn(Optional.of(existOwner));

        ownerService.updateOwnerProfile(userId, r);
        assertEquals(aboutMe, existOwner.getAboutMe());
        verify(ownerRepository).save(existOwner);
    }


    @Test 
    public void deleteOwner_ShouldRemoveLinkAndDelete(){
        Long userId = 1L;
        User user = new User();
        Owner owner = new Owner(user);
        owner.setUserId(userId);
        user.setOwner(owner);

        when(ownerRepository.findById(userId)).thenReturn(Optional.of(owner));
        ownerService.deleteOwner(userId);
        assertNull(user.getOwner());

        verify(ownerRepository).delete(owner);
    }

    @Test
    void addPet_ShouldCallPetService(){

        Long ownerId = 1L;
        Owner owner = new Owner();
        owner.setUserId(ownerId);
        
        PetRequest petRequest = new PetRequest();
        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        ownerService.addPet(ownerId, petRequest);

        verify(ownerRepository).findById(ownerId);
        verify(petService).savePet(owner, petRequest);
    }

    @Test
    void getAllPets_ShouldDelegateToPetService(){
        Long ownerId = 1L;
        List<Pet> expectedPets = List.of(new Pet(), new Pet());
        when(petService.getAllPetsByOwner(ownerId)).thenReturn(expectedPets);

        List<Pet> result = ownerService.getAllPets(ownerId);

        assertThat(result).hasSize(2);
        assertEquals(expectedPets, result);
        verify(petService).getAllPetsByOwner(ownerId);
    }
    
    @Test
    void updatePet_ShouldDelegateToPetService() {
        Long ownerId = 1L;
        String slug = "dog-slug";
        PetRequest updateRequest = new PetRequest();
        ownerService.updatePet(ownerId, slug, updateRequest);
        verify(petService).updatePetByOwner(ownerId, slug, updateRequest);
    }

}
