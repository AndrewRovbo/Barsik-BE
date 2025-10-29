package com.barsik.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.SitterRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class SitterService {

    @Autowired private SitterRepository sitterRepository;
    
    @Transactional
    public void updateSitterProfile(Long userId, SitterProfileUpdateRequest request) {
        Sitter sitter = sitterRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Sitter profile not found"));

        if (request.getExperienceSummary() != null) {
            sitter.setExperienceSummary(request.getExperienceSummary());
        }

        sitterRepository.save(sitter);
        //userProfileService.updateTimestamp(userId);
    }

    @Transactional
    public void deleteSitter(Long userId) {
        Sitter sitter = sitterRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Sitter not found"));

        User user = sitter.getUser();
        user.setSitter(null);
        sitterRepository.delete(sitter);
    }

    public boolean existsByUserId(Long userId) {
        return sitterRepository.existsById(userId);
    }

    public Sitter getByUserId(Long userId) {
        return sitterRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Sitter not found"));
    }
}
