package com.Arisuki.log.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Arisuki.log.entity.CommentEntity;
import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.entity.UserEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity , Integer> {
    // そのユーザーがすでにコメントしているか
    boolean existsByInformationAndUser(
            InformationEntity information,
            UserEntity user
    );

    // 編集用（自分のコメント取得）
    Optional<CommentEntity> findByInformationAndUser(
            InformationEntity information,
            UserEntity user
    );

    // 作品のコメント一覧
    //投稿順
    List<CommentEntity> findByInformationOrderByCreatedAtDesc(
            InformationEntity information
    );

    // コメント数表示用
    long countByInformation(
            InformationEntity information
    );
}

