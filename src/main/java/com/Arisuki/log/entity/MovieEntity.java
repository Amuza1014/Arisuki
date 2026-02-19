package com.Arisuki.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class MovieEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
	
	// タイトル
	private String title;
	  // HTMLの name="thumbnailUrl" と合わせる
    private String thumbnailUrl;
    // HTMLの name="reviewText" と合わせる
    private String reviewText;
    
    // 監督
    private String director;
    // 洋画
    private String westernMovie;
    // 邦画
    private String japaneseMovie;
    // カテゴリー
    private String category;
}
