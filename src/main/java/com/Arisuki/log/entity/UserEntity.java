package com.Arisuki.log.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザー名（ログインIDとして使用。重複不可に設定）
    @Column(nullable = false, unique = true)
    private String username;

    // パスワード（ハッシュ化して保存するため長めに確保）
    @Column(nullable = false)
    private String password;

    // ユーザーの表示名
    private String displayName;

    // 権限（一般ユーザー：ROLE_USER, 管理者：ROLE_ADMINなど）
    private String role;

    // 【重要】このユーザーが投稿した作品一覧との紐付け（1対多）
    // 後ほどInformationEntity側にもUserEntityを持たせる修正を行います
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InformationEntity> posts;
}