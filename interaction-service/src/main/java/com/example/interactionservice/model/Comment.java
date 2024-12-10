package com.example.interactionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_body", nullable = false)
    private String commentBody;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "comment_likes", // Name for the join table
            joinColumns = @JoinColumn(name = "comment_id"), // Foreign key to the comment table
            inverseJoinColumns = @JoinColumn(name = "like_id") // Foreign key to the like table
    )
    private List<Like> likes = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "comment_replies", // Clearer name for recursive relation
            joinColumns = @JoinColumn(name = "parent_comment_id"), // Foreign key to the parent comment
            inverseJoinColumns = @JoinColumn(name = "child_comment_id") // Foreign key to the child comment
    )
    private List<Comment> replies = new ArrayList<>();

    @Column(name = "post_id")
    private Long postId;
    @Column(name = "isDeletedFlag", nullable = false)
    private Boolean isDeleted;
}
