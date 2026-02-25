package com.barsik.backend.service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.SitterRepository;
import com.barsik.backend.service.SitterService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SitterServiceTest {
    @Mock
    private SitterRepository sitterRepository;

    @InjectMocks
    private SitterService sitterService;

    @BeforeAll
    public static void BeforeAll(){
        MockitoAnnotations.openMocks(SitterServiceTest.class);
    }

    @Test
    void updateSitterProfile_ShouldUpdateFields(){
        Long userId = 1L;
        User user = new User();
        Sitter sitter = new Sitter(user);
        sitter.setUserId(userId);
        user.setSitter(sitter);
        sitter.setExperienceSummary("Old Experience");
        SitterProfileUpdateRequest req = new SitterProfileUpdateRequest();
        req.setExperienceSummary("new fuller expirience");
        when(sitterRepository.findById(userId)).thenReturn(Optional.of(sitter));

        sitterService.updateSitterProfile(userId, req);

        // 3. Assert
        assertEquals(sitter.getExperienceSummary(), "new fuller expirience");
        verify(sitterRepository).save(sitter);
    }

    @Test
    void updateSitterProfile_ShouldThrowENtityNotFoundException(){
        Long userId = 99L;

        SitterProfileUpdateRequest req = new SitterProfileUpdateRequest();
        req.setExperienceSummary("new fuller expirience");
        when(sitterRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> sitterService.updateSitterProfile(userId, req));
        
        verify(sitterRepository, never()).save(any());
    }
    @Test
    void deleteSitter_ShouldRemoveLinkAndDelete(){
        Long userId = 1L;
        User user = new User();
        Sitter sitter = new Sitter(user);
        sitter.setUserId(userId);
        user.setSitter(sitter);

        when(sitterRepository.findById(userId)).thenReturn(Optional.of(sitter));

        sitterService.deleteSitter(userId);

        assertNull(user.getSitter());

        verify(sitterRepository).delete(sitter);
    }
    @Test
    void getByUserId_shouldReturnSitterWhenExists(){
        Long id = 1L;
        User user = new User();
        Sitter sitter = new Sitter(user);
        sitter.setUserId(id);
        user.setSitter(sitter);

        when(sitterRepository.findById(id)).thenReturn(Optional.of(sitter));

        Sitter actual = sitterService.getByUserId(id);

        assertEquals(sitter, actual);
    }
    @Test
    void getByUserId_ShouldThrowExceptionWhenNotFound(){
        Long userId = 99L;
        when(sitterRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sitterService.getByUserId(userId));

        verify(sitterRepository, times(1)).findById(userId);
    
    }

}
