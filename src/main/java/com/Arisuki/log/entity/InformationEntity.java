package com.Arisuki.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity // これを忘れるとDBのテーブルになりません
@Data
public class InformationEntity {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	    
	    private String title;
	    // HTMLの name="thumbnailUrl" と合わせる
	    private String thumbnailUrl;
	    // HTMLの name="reviewText" と合わせる
	    private String reviewText;

	    // 以下、将来的に使う項目として残してOK
//	    private String author;
//	    private String publisher;
//	    private String category;
	    
	    private String creater;
	    private String category;
	    private String publisher;
	    private String subAttribute;
}
