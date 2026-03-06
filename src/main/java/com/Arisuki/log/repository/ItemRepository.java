package com.Arisuki.log.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Arisuki.log.entity.InformationEntity;

@Repository
public interface ItemRepository extends JpaRepository<InformationEntity, Integer> {
    
    // マイページ用：特定のユーザーの投稿を取得
    // ※UserEntityのIDがLongならLong、IntegerならIntegerに合わせます
    List<InformationEntity> findByUserId(Long userId);

    // タイムライン用：全投稿をIDの降順（新しい順）で取得
    List<InformationEntity> findAllByOrderByIdDesc();
    
    @Query("SELECT i FROM InformationEntity i WHERE " +
            "(:category IS NULL OR :category = '' OR i.category = :category) AND " +
            "(:keyword IS NULL OR :keyword = '' OR i.title LIKE %:keyword% OR i.creator LIKE %:keyword%) " +
            "ORDER BY i.id DESC")
     List<InformationEntity> searchItems(@Param("category") String category, @Param("keyword") String keyword);
 
}