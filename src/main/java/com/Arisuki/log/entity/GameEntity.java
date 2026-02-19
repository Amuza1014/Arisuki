package com.Arisuki.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class GameEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
	
	// タイトル
	private String title;
	  // HTMLの name="thumbnailUrl" と合わせる
    private String thumbnailUrl;
    // HTMLの name="reviewText" と合わせる
    private String reviewText;
    
    // メーカー
    private String maker;
    // ゲームのハード
    private String gameConsole;
    // カテゴリー
    private String category;

}
