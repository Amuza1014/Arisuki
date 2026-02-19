package com.Arisuki.log.entity;

<<<<<<< HEAD
import lombok.Data;

@Data
public class BookEntity {
	/**ID*/
	private Integer id;
	/**タイトル*/
	private String title;
	/**著者*/
	private String author;
	/**出版社*/
	private String publisher;
	/**ジャンル*/
	private String category;
	/**レビュー*/
	private String review;
	/**イメージ*/
	private String image;

=======
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity // これを忘れるとDBのテーブルになりません
@Data
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    // HTMLの name="thumbnailUrl" と合わせる
    private String thumbnailUrl;
    // HTMLの name="reviewText" と合わせる
    private String reviewText;

    // 以下、将来的に使う項目として残してOK
    private String author;
    private String publisher;
    private String category;
>>>>>>> branch 'master' of https://github.com/Amuza1014/Arisuki.git
}