package com.Arisuki.log.entity;

<<<<<<< HEAD
import jakarta.persistence.Column;
=======
import java.util.List;

import jakarta.persistence.CascadeType;
>>>>>>> refs/remotes/origin/dai-table
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
<<<<<<< HEAD
	@Column(nullable = false)
	private Integer score;//5点満点

	// 【追加】投稿したユーザーとの紐付け
	@ManyToOne
	@JoinColumn(name = "user_id") // DB内では user_id というカラムになります
	private UserEntity user;

}
=======

	private Integer score;

	private Integer scoreSum;
	private Integer scoreCount;
	
	 // 【追加】投稿したユーザーとの紐付け
    @ManyToOne
    @JoinColumn(name = "user_id") // DB内では user_id というカラムになります
    private UserEntity user;
    
    @OneToMany(mappedBy = "information", cascade = CascadeType.ALL)
    private List<CommentEntity> comments;
    
   // 平均を求めるメソッド
    public double getAverageScore() {
        if (scoreCount == null || scoreCount == 0) {
            return 0.0;
        }
        return (double) scoreSum / scoreCount;
    }
    
}
>>>>>>> refs/remotes/origin/dai-table
