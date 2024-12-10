package com.example.interactionservice.repositories;

import com.example.interactionservice.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllLikesByPostId(Long postId);

    Like findByPostIdAndUserId(Long userId, Long postId);
}
