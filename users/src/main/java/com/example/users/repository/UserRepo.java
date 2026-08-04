package com.example.users.repository;

import com.example.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
    UserEntity findByEmail(String email);
}
