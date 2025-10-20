package com.barsik.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.barsik.backend.api.DTO.request.RegistrationRequestLong;
import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.entity.Owner;
import com.barsik.backend.entity.Sitter;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.OwnerRepository;
import com.barsik.backend.repository.SitterRepository;
import com.barsik.backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private SitterRepository sitterRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User saveUser(User user){
        return userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Owner profile not found"));
    }
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest dto){
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        applyIfPresent(dto.getEmail(), user::setEmail);
        applyIfPresent(dto.getFirstName(), user::setFirstName);
        applyIfPresent(dto.getLastName(), user::setLastName);
        applyIfPresent(dto.getPhoneNumber(), user::setPhoneNumber);
        applyIfPresent(dto.getAvatarUrl(), user::setAvatarUrl);
        applyIfPresent(dto.getAddress(), user::setAddress);

        return userRepository.save(user);
    }
    private <T> void applyIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Transactional
    public void touchUpdatedAt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.save(user);
    }
    @Transactional
    public void registerUser(RegistrationRequestLong request) {
        /* Вы используете @ExceptionHandler для перехвата DataIntegrityViolationException и преобразования в 400.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConstraintViolationException("User with this email already exists");
        }*/

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        if (request.getRole() == null) {
            throw new IllegalArgumentException("User role must be specified (OWNER or SITTER)");
        }

        switch (request.getRole()) {
            case OWNER -> {
                Owner owner = new Owner();
                owner.setUser(user);
                //owner.setIsVerified(false);
                ownerRepository.save(owner);
            }
            case SITTER -> {
                Sitter sitter = new Sitter();
                sitter.setUser(user);
                sitter.setAverageRating(BigDecimal.ZERO);
                sitter.setReviewsCount(0);
                //sitter.setIsVerified(false);
                sitterRepository.save(sitter);
            }
            default -> throw new IllegalArgumentException("Unsupported role: " + request.getRole());
        }
    }
    
}
