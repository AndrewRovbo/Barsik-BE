package com.barsik.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired private UserRepository userRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private SitterRepository sitterRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User saveUser(User user){
        logger.trace("Enter saveUser(userId={})", user != null ? user.getId() : null);
        try {
            logger.debug("Saving user (email={}, username={})",
                    user != null ? user.getEmail() : null,
                    user != null ? user.getFirstName() + " " + user.getLastName() : null);
            User saved = userRepository.save(user);
            logger.info("User saved successfully, id={}", saved != null ? saved.getId() : null);
            return saved;
        } catch (Exception ex) {
            logger.error("Failed to save user", ex);
            throw ex;
        } finally {
            logger.trace("Exit saveUser");
        }
    }

    public User findByEmail(String email) {
        logger.trace("Enter findByEmail(email={})", email);
        try {
            return userRepository.findByEmail(email)
                    .map(u -> {
                        logger.info("User found by email: {}", email);
                        logger.debug("Found user id={}, username={}", u.getId(), u.getFirstName() + " " + u.getLastName());
                        return u;
                    })
                    .orElseThrow(() -> {
                        logger.warn("User not found by email: {}", email);
                        return new RuntimeException("Owner profile not found");
                    });
        } catch (RuntimeException ex) {
            logger.error("Error in findByEmail for email={}", email, ex);
            throw ex;
        } finally {
            logger.trace("Exit findByEmail");
        }
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest dto){
        logger.trace("Enter updateUser(userId={})", userId);
        logger.debug("Update payload: {}", dto);
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found for update, id={}", userId);
                    return new RuntimeException("User not found");
                });

            logger.debug("Existing user before update: id={}, email={}, firstName={}, lastName={}, phone={}",
                    user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber());

            applyIfPresent(dto.getEmail(), user::setEmail, "email");
            applyIfPresent(dto.getFirstName(), user::setFirstName, "firstName");
            applyIfPresent(dto.getLastName(), user::setLastName, "lastName");
            applyIfPresent(dto.getPhoneNumber(), user::setPhoneNumber, "phoneNumber");
            applyIfPresent(dto.getAvatarUrl(), user::setAvatarUrl, "avatarUrl");
            applyIfPresent(dto.getAddress(), user::setAddress, "address");

            user.setUpdatedAt(LocalDateTime.now());
            User saved = userRepository.save(user);

            logger.info("User updated successfully, id={}", saved.getId());
            logger.debug("User after update: id={}, email={}, firstName={}, lastName={}, phone={}",
                    saved.getId(), saved.getEmail(), saved.getFirstName(), saved.getLastName(), saved.getPhoneNumber());

            return saved;
        } catch (RuntimeException ex) {
            logger.error("Failed to update user id={}", userId, ex);
            throw ex;
        } finally {
            logger.trace("Exit updateUser");
        }
    }

    private <T> void applyIfPresent(T value, Consumer<T> setter, String fieldName) {
        if (value != null) {
            logger.debug("Updating field '{}' to value='{}'", fieldName, value);
            setter.accept(value);
        }
    }

    public User findById(Long id){
        logger.trace("Enter findById(id={})", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("User not found by id={}", id);
                        return new RuntimeException("User not found");
                    });
            logger.info("User found by id={}", id);
            logger.debug("Found user details: id={}, email={}, name={}", user.getId(), user.getEmail(), user.getFirstName() + " " + user.getLastName());
            return user;
        } catch (RuntimeException ex) {
            logger.error("Error in findById for id={}", id, ex);
            throw ex;
        } finally {
            logger.trace("Exit findById");
        }
    }

    @Transactional
    public void touchUpdatedAt(Long userId) {
        logger.trace("Enter touchUpdatedAt(userId={})", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("User not found in touchUpdatedAt, id={}", userId);
                        return new RuntimeException("User not found");
                    });
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            logger.info("Touched updatedAt for user id={}", userId);
        } catch (RuntimeException ex) {
            logger.error("Failed to touch updatedAt for user id={}", userId, ex);
            throw ex;
        } finally {
            logger.trace("Exit touchUpdatedAt");
        }
    }

    @Transactional
    public void registerUser(RegistrationRequestLong request) {
        logger.trace("Enter registerUser");
        logger.debug("Registration request email={}, role={}", request != null ? request.getEmail() : null, request != null ? request.getRole() : null);

        try {
            // if (userRepository.existsByEmail(request.getEmail())) {
            //     logger.warn("Attempt to register already existing email={}", request.getEmail());
            //     throw new ConstraintViolationException("User with this email already exists");
            // }

            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setPhoneNumber(request.getPhoneNumber());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            user = userRepository.save(user);
            logger.info("Registered new user, id={}, email={}", user.getId(), user.getEmail());

            if (request.getRole() == null) {
                logger.warn("Registration request missing role for email={}", request.getEmail());
                throw new IllegalArgumentException("User role must be specified (OWNER or SITTER)");
            }

            switch (request.getRole()) {
                case OWNER -> {
                    Owner owner = new Owner();
                    owner.setUser(user);
                    // owner.setIsVerified(false);
                    ownerRepository.save(owner);
                    logger.info("Created Owner profile for user id={}", user.getId());
                }
                case SITTER -> {
                    Sitter sitter = new Sitter();
                    sitter.setUser(user);
                    sitter.setAverageRating(BigDecimal.ZERO);
                    sitter.setReviewsCount(0);
                    // sitter.setIsVerified(false);
                    sitterRepository.save(sitter);
                    logger.info("Created Sitter profile for user id={}", user.getId());
                }
                default -> {
                    logger.error("Unsupported role '{}' in registration for user id={}", request.getRole(), user.getId());
                    throw new IllegalArgumentException("Unsupported role: " + request.getRole());
                }
            }
        } catch (RuntimeException ex) {
            logger.error("Failed to register user (email={})", request != null ? request.getEmail() : null, ex);
            throw ex;
        } finally {
            logger.trace("Exit registerUser");
        }
    }

}
