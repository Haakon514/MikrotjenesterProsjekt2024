package com.example.communityservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommunityUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(unique = true)
    private String username;
    @Column(nullable = false)
    private Long communityId;
    private LocalDateTime joinedAt = LocalDateTime.now();

}
