package com.barsik.backend.service;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barsik.backend.api.DTO.request.UserUpdateRequest;
import com.barsik.backend.entity.User;
import com.barsik.backend.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
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
    public void deleteUserById(Long userId){
        userRepository.deleteById(userId);
    }
    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Transactional
    public void touchUpdatedAt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Можно просто сохранить, чтобы сработал @PreUpdate
        userRepository.save(user);
    }
    
}
