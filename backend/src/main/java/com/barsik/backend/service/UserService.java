package com.barsik.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.barsik.backend.entity.User;
import com.barsik.backend.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }
    public void deleteUserById(Long userId){
        userRepository.deleteById(userId);
    }
    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }
    
}
