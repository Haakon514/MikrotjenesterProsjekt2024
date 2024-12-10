package com.example.interactionservice.service;

import com.example.interactionservice.clients.PostClient;
import com.example.interactionservice.clients.UserClient;
import com.example.interactionservice.model.Comment;
import com.example.interactionservice.model.Like;
import com.example.interactionservice.repositories.CommentRepository;
import com.example.interactionservice.repositories.LikeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class InteractionService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    //private final PostClient postClient;
    //private final UserClient userClient;

    public List<Like> getLikesFromPost(Long post_id){
        /*Boolean checkPostExist = postClient.checkPostExist(post_id);
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }*/

        List<Like> likesFromPost = likeRepository.findAllLikesByPostId(post_id);
        List<Like> likesWithoutCommentLikes = new ArrayList<>();

        for (int i = 0; i < likesFromPost.size(); i++){
            if (likesFromPost.get(i).getCommentId() == null){
                likesWithoutCommentLikes.add(likesFromPost.get(i));
            }
        }

        return likesWithoutCommentLikes;
    }

    public List<Comment> getCommentsFromPost(Long post_id){
        /*Boolean checkPostExist = postClient.checkPostExist(post_id);
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }*/

        List<Comment> commentsFromPost = commentRepository.findAllCommentsByPostId(post_id);
        List<Comment> commentsFromPostWithoutReplies = new ArrayList<>();

        for (int i = 0; i < commentsFromPost.size(); i++){
            if (commentsFromPost.get(i).getParentCommentId() == null){
                commentsFromPostWithoutReplies.add(commentsFromPost.get(i));
            }
        }

        return commentsFromPostWithoutReplies;
    }

    public Integer getAmountOfLikesOnComment(Long comment_id){
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("no comment found"));

        return comment.getLikes().size();
    }

    public List<Like> getLikesFromComment(Long comment_id){
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("no comment found"));

        return comment.getLikes();
    }

    public List<Comment> getRepliesFromComment(Long comment_id){
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("no comment found"));

        return comment.getReplies();
    }

    public void likePost(Long user_id, Long post_id){
        /*Boolean checkPostExist = postClient.checkPostExist(post_id);
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        Like likeTemp = likeRepository.findByPostIdAndUserId(user_id, post_id);

        if (likeTemp != null){
            throw new RuntimeException("post is already liked");
        }

        Like like = new Like();
        like.setUserId(user_id);
        like.setPostId(post_id);
        like.setCommentId(null);
        likeRepository.save(like);
    }

    public void likeComment(Long user_id, Long comment_id){

        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("comment not found"));

        for (int i = 0; i < comment.getLikes().size(); i++){
            if (comment.getLikes().get(i).getUserId().equals(user_id)){
                throw new RuntimeException("comment is already liked");
            }
        }

        /*Boolean checkPostExist = postClient.checkPostExist(comment.getPostId());
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        Like like = new Like();
        like.setUserId(user_id);
        like.setPostId(comment.getPostId());
        like.setCommentId(comment.getId());
        likeRepository.save(like);

        comment.getLikes().add(like);
        commentRepository.save(comment);
    }

    public Boolean unLikeComment(Long user_id, Long comment_id){

        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("no comment found"));

        /*Boolean checkPostExist = postClient.checkPostExist(comment.getPostId());
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        if(comment.getUserId().equals(user_id)){
            for (int i = 0; i < comment.getLikes().size(); i++){
                if (comment.getLikes().get(i).getUserId().equals(comment.getUserId())){
                    Like likeToDelete = comment.getLikes().get(i);
                    comment.getLikes().remove(likeToDelete);
                    commentRepository.save(comment);
                    likeRepository.delete(likeToDelete);
                    return true;
                }
            }
        }

        return false;
    }

    public void commentOnPost(Long user_id, Long post_id, String comment_body){
        /*Boolean checkPostExist = postClient.checkPostExist(post_id);
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        Comment comment = new Comment();
        comment.setPostId(post_id);
        comment.setUserId(user_id);
        comment.setIsDeleted(false);
        comment.setCommentBody(comment_body);
        commentRepository.save(comment);
    }

    public void replyToComment(Long user_id, Long comment_id, String comment_body) {
        /*Boolean checkPostExist = postClient.checkPostExist(post_id);
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        Comment parentComment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment newReply = new Comment();
        newReply.setUserId(user_id);
        newReply.setPostId(null);
        newReply.setIsDeleted(false);
        newReply.setParentCommentId(comment_id);
        newReply.setCommentBody(comment_body);
        commentRepository.save(newReply);

        parentComment.getReplies().add(newReply);
        commentRepository.save(parentComment);
    }

    public Boolean unLikePost(Long user_id, Long post_id){

        Like likeToDelete = likeRepository.findByPostIdAndUserId(user_id, post_id);

        /*Boolean checkPostExist = postClient.checkPostExist(likeToDelete.getPostId());
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        if (likeToDelete.getUserId().equals(user_id)){
            likeRepository.delete(likeToDelete);
            return true;
        }

        return false;
    }

    public Boolean deleteComment(Long user_id, Long comment_id){

        Comment commentToDelete = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("no comment found"));

        /*Boolean checkPostExist = postClient.checkPostExist(commentToDelete.getPostId());
        if (!checkPostExist){
            throw new RuntimeException("post doesnt exist");
        }

        Boolean checkUserExist = userClient.checkUserExist(user_id);
        if (!checkUserExist){
            throw new RuntimeException("user doesnt exist");
        }*/

        //delete comment with a parent comment
        if (commentToDelete.getParentCommentId() != null){

            Comment parentComment = commentRepository.findById(commentToDelete.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("no parent comment found"));

            if (!commentToDelete.getUserId().equals(user_id)){
                return false;
            }

            if (commentToDelete.getReplies().size() < 1 && commentToDelete.getLikes().size() < 1){

                parentComment.getReplies().remove(commentToDelete);
                commentRepository.save(parentComment);
                commentRepository.delete(commentToDelete);
                return true;
            }

            commentToDelete.setCommentBody("comment removed");
            commentToDelete.setIsDeleted(true);
            commentRepository.save(commentToDelete);
            return true;
        }

        //delete comment without parent comment
        if (!commentToDelete.getUserId().equals(user_id)){
            return false;
        }

        if (commentToDelete.getReplies().size() < 1 && commentToDelete.getLikes().size() < 1){
            commentRepository.delete(commentToDelete);
            return true;
        }

        commentToDelete.setCommentBody("comment removed");
        commentToDelete.setIsDeleted(true);
        commentRepository.save(commentToDelete);
        return true;
    }
}
