package com.barsik.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.request.OwnerProfileUpdateRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.repository.OwnerRepository;

@Service
public class OwnerService {
    
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private UserService userService;
    
    public boolean isUserOwner(Long userId) {
        return ownerRepository.existsByUserId(userId);
    }
    
    public Owner getByUserId(Long userId){return ownerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));}
    public void updateOwnerProfile(Long userId, OwnerProfileUpdateRequest request) {
        Owner owner = ownerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));
        
        if (request.getAboutMe() != null) {
            owner.setAboutMe(request.getAboutMe());
        }
        ownerRepository.save(owner);
        userService.touchUpdatedAt(userId);
    }
    @Transactional
    public Owner createOwnerProfile(Owner owner) {
        Long userId = owner.getUser().getId();
        if (ownerRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Owner profile already exists for user " + userId);
        }
        // Любые дополнительные инициализации полей owner можно сделать здесь
        return ownerRepository.save(owner);
    }
}
