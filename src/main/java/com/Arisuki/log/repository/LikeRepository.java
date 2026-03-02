package com.Arisuki.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Arisuki.log.entity.InformationEntity;
import com.Arisuki.log.entity.LikeEntity;
import com.Arisuki.log.entity.UserEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {
	long countByInformation(InformationEntity information);

	boolean existsByInformationAndUser(InformationEntity information, UserEntity user);

	void deleteByInformationAndUser(InformationEntity information, UserEntity user);
}
