package com.barsik.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.SitterRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
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



    public List<Sitter> searchSitters(String experienceKeyword, BigDecimal minRating, Boolean isVerified) {
        Specification<Sitter> spec = filterByCriteria(experienceKeyword, minRating, isVerified);
        return sitterRepository.findAll(spec);
    }
    
    public static Specification<Sitter> filterByCriteria(String experienceKeyword, BigDecimal minRating,
                                                        Boolean isVerified) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по опыту (experienceSummary содержит ключевое слово)
            if (experienceKeyword != null && !experienceKeyword.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("experienceSummary")), "%" + experienceKeyword.toLowerCase() + "%"));
            }

            // Фильтр по минимальному рейтингу
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
            }

            // Фильтр по статусу верификации
            if (isVerified != null) {
                predicates.add(cb.equal(root.get("isVerified"), isVerified));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
