package com.Arisuki.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

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
	
	
	 // 【追加】投稿したユーザーとの紐付け
    @ManyToOne
    @JoinColumn(name = "user_id") // DB内では user_id というカラムになります
    private UserEntity user;
}
