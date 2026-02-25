package com.barsik.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.request.SitterProfileUpdateRequest;
import com.barsik.backend.api.DTO.response.SitterResponseDTO;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.SitterRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;


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

    @Transactional(readOnly=true)
    public Page<SitterResponseDTO> searchSitters(String serviceType, String experienceKeyword, 
                                                 BigDecimal minRating, Boolean isVerified, 
                                                 Pageable pageable) {
                                                     
        Specification<Sitter> spec = filterByCriteria(serviceType, experienceKeyword, minRating, isVerified);
        Page<Sitter> sittersPage = sitterRepository.findAll(spec, pageable);
        return sittersPage.map(this::mapToDTO);
    }

    public static Specification<Sitter> filterByCriteria(String serviceType, String experienceKeyword, 
                                                         BigDecimal minRating, Boolean isVerified) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            if (serviceType != null && !serviceType.trim().isEmpty()) {
                Join<Sitter, PetService> serviceJoin = root.join("services", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(serviceJoin.get("name")), serviceType.toLowerCase()));
            }
            if (experienceKeyword != null && !experienceKeyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("experienceSummary")), "%" + experienceKeyword.toLowerCase() + "%"));
            }
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
            }
            if (isVerified != null) {
                predicates.add(cb.equal(root.get("isVerified"), isVerified));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    private SitterResponseDTO mapToDTO(Sitter sitter) {
        SitterResponseDTO dto = new SitterResponseDTO();
        dto.setUserId(sitter.getUserId());
        dto.setExperienceSummary(sitter.getExperienceSummary());
        dto.setAverageRating(sitter.getAverageRating());
        dto.setReviewsCount(sitter.getReviewsCount());
        dto.setIsVerified(sitter.getIsVerified());
        return dto;
    }
}
