package com.barsik.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barsik.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    //@Modifying
    //@Query(value = "DELETE FROM users WHERE id = ?1", nativeQuery = true)
    void deleteById(long id);
}
