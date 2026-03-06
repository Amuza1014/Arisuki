package com.Arisuki.log.entity;

import java.util.List; // 追加

import jakarta.persistence.CascadeType; // 追加
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany; // 追加

import lombok.Data;
import lombok.EqualsAndHashCode; // 追加
import lombok.ToString; // 追加

@Entity
@Data
public class InformationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String thumbnailUrl;
    private String reviewText;
    private String creator;
    private String category;
    private String publisher;
    private String subAttribute;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // --- ここを追加 ---
    // mappedBy は LikeEntity 側にある "information" フィールド名を指します
    @OneToMany(mappedBy = "information", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // 循環参照（Lombokの無限ループ）を防止
    @EqualsAndHashCode.Exclude // 循環参照を防止
    private List<LikeEntity> likes;

    @OneToMany(mappedBy = "information", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // 循環参照を防止
    @EqualsAndHashCode.Exclude // 循環参照を防止
    private List<CommentEntity> comments;
}