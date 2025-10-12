package com.barsik.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.repository.SitterRepository;

@Service
public class SitterService {
    
    @Autowired private SitterRepository sitterRepository;
    @Autowired private UserService userService;
    public boolean isUserSitter(Long userId) {
        return sitterRepository.existsByUserId(userId);
    }
    public Sitter getByUserId(Long userId){
        return sitterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Sitter profile not found"));
    }
    public void updateSitterProfile(Long userId, SitterProfileUpdateRequest request) {
        Sitter sitter = sitterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Sitter profile not found"));
        
        if (request.getExperienceSummary() != null) {
            sitter.setExperienceSummary(request.getExperienceSummary());
        }
        
        sitterRepository.save(sitter);
        userService.touchUpdatedAt(userId);
    }
    public void deleteSitterProfile(Long userId){
        sitterRepository.deleteById(userId);
    }
    @Transactional
    public Sitter createSitterProfile(Sitter sitter) {
        Long userId = sitter.getUser().getId();
        if (sitterRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Owner profile already exists for user " + userId);
        }
        // Любые дополнительные инициализации полей owner можно сделать здесь
        return sitterRepository.save(sitter);
    }
}
