package com.example.interactionservice.controller;

import com.example.interactionservice.model.Like;
import com.example.interactionservice.service.InteractionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/likes")
public class InteractionLikesController{

    private final InteractionService interactionService;

    @GetMapping("/getAmountOfLikesOnPost/{post_id}")
    public ResponseEntity<Integer> getAmountOfLikesOnPost(@PathVariable Long post_id){
        List<Like> likes = interactionService.getLikesFromPost(post_id);
        int amountOfLikes = likes.size();

        if (likes.isEmpty()){
            return new ResponseEntity<>(amountOfLikes, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(amountOfLikes, HttpStatus.OK);
    }

    @GetMapping("/getAmountOfLikesOnComment/{comment_id}")
    public ResponseEntity<Integer> getAmountOfLikesOnComment(@PathVariable Long comment_id){
        int amount = interactionService.getAmountOfLikesOnComment(comment_id);
        return new ResponseEntity<>(amount, HttpStatus.OK);
    }

    @GetMapping("/getLikesFromComment/{comment_id}")
    public ResponseEntity<List<Like>> getLikesFromComment(@PathVariable Long comment_id) {
        List<Like> commentLikes = interactionService.getLikesFromComment(comment_id);

        if (commentLikes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(commentLikes, HttpStatus.OK);
    }

    @GetMapping("/getLikes/{post_id}")
    public ResponseEntity<List<Like>> getLikesFromPost(@PathVariable Long post_id){
        List<Like> likes = interactionService.getLikesFromPost(post_id);
        if(likes != null){
            return new ResponseEntity<>(likes, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/userId/{user_id}/commentId/{comment_id}/like")
    public ResponseEntity<HttpStatus> likeComment(@PathVariable Long user_id, @PathVariable Long comment_id){
        interactionService.likeComment(user_id, comment_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/userId/{user_id}/commentId/{comment_id}/unlike")
    public ResponseEntity<HttpStatus> unLikeComment(@PathVariable Long user_id, @PathVariable Long comment_id){
        Boolean check = interactionService.unLikeComment(user_id, comment_id);
        if (check){
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/userId/{user_id}/postId/{post_id}/like")
    public ResponseEntity<HttpStatus> likePost(@PathVariable Long user_id, @PathVariable Long post_id){
        interactionService.likePost(user_id, post_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/userId/{user_id}/postId/{post_id}/unlike")
    public ResponseEntity<HttpStatus> unlikePost(@PathVariable Long user_id, @PathVariable Long post_id){
        Boolean trueIfLikeDeleted = interactionService.unLikePost(user_id, post_id);
        if (trueIfLikeDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

