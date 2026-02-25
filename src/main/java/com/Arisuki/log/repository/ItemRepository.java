package com.Arisuki.log.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Arisuki.log.entity.InformationEntity;

@Repository
public interface ItemRepository extends JpaRepository<InformationEntity, Integer> {
	
	List<InformationEntity> findByUserId(Long userId);
    
}