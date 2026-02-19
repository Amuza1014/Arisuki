package com.Arisuki.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Arisuki.log.entity.InformationEntity;

@Repository
public interface BookRepository extends JpaRepository<InformationEntity, Integer> {
    
}