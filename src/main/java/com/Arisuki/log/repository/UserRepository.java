package com.Arisuki.log.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Arisuki.log.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // ログイン時にユーザー名でDBを検索するためのメソッド
    Optional<UserEntity> findByUsername(String username);
}