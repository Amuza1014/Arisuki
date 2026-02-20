package com.Arisuki.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
}

