package com.Arisuki.log.entity;

import jakarta.persistence.Column;
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
	//	 // 【追加】投稿したユーザーとの紐付け
	//	    @ManyToOne
	//	    @JoinColumn(name = "user_id") // DB内では user_id というカラムになります
	//	    private InformationEntity user;
	//	
	@Column(nullable = false)
	private Integer score;//5点満点

	@Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    // --- 削除：循環参照の原因だった以下のフィールドを消します ---
    // @OneToMany(mappedBy = "information", cascade = CascadeType.ALL)
    // private Set<CommentEntity> comments;
    //
    // @OneToMany(mappedBy = "information", cascade = CascadeType.ALL)
    // private Set<LikeEntity> likes;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}

