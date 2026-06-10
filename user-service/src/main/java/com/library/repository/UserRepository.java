package com.library.repository;

import com.library.model.User;
import com.library.model.User.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findByNameContainingIgnoreCase(String name);
    boolean existsByEmail(String email);
}
