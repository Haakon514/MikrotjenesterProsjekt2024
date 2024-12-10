package com.example.communityservice.repositories;

import com.example.communityservice.model.CommunityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityUserRepository extends JpaRepository<CommunityUser, Long> {
    Optional<CommunityUser> findByUserIdAndCommunityId(Long userId, Long communityId);
    Optional<List<CommunityUser>> findByUserId(Long userId);
}
