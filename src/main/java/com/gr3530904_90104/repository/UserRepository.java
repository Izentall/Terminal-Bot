package com.gr3530904_90104.repository;

import com.gr3530904_90104.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserById(Integer id);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByChatId(Long chatId);
}
