package com.Arisuki.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Data
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"information_id", "user_id"}
    )
)
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // どの投稿か
    @ManyToOne
    @JoinColumn(name = "information_id")
    private InformationEntity information;

    // 誰が押したか
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
