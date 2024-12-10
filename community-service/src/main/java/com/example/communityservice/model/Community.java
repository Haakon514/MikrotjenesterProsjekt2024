package com.example.communityservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 225)
    private String description;

    @Column(nullable = false)
    private Boolean isPrivate;

    // Many communities can have one creator
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private CommunityUser createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Many-to-Many relationship with rules
    @ManyToMany
    @JoinTable(
            name = "community_rules",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private List<Rule> rules = new ArrayList<>();

    // Many-to-Many relationship with members
    @ManyToMany
    @JoinTable(
            name = "community_members", // Ensure this is unique
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<CommunityUser> members = new ArrayList<>();

    // Many-to-Many relationship with moderators
    @ManyToMany
    @JoinTable(
            name = "community_moderators", // Ensure this is unique
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "moderator_id")
    )
    private List<CommunityUser> moderators = new ArrayList<>();

}
