package com.Arisuki.log.entity;

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

}