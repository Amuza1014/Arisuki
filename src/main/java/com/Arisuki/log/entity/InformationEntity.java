package com.Arisuki.log.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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
	//	 // 【追加】投稿したユーザーとの紐付け
	//	    @ManyToOne
	//	    @JoinColumn(name = "user_id") // DB内では user_id というカラムになります
	//	    private InformationEntity user;
	//	
	@Column(nullable = false)
	private Integer score;//5点満点

	// 【追加】投稿したユーザーとの紐付け
	@ManyToOne
	@JoinColumn(name = "user_id") // DB内では user_id というカラムになります
	private UserEntity user;

	@OneToMany(mappedBy = "information", cascade = CascadeType.ALL)
	private List<CommentEntity> comments;

}
