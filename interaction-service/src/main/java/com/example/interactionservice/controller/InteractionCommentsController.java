package com.example.interactionservice.controller;

import com.example.interactionservice.model.Comment;
import com.example.interactionservice.service.InteractionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class InteractionCommentsController {

    private final InteractionService interactionService;

    @GetMapping("/getAmountOfComments/{post_id}")
    public ResponseEntity<Integer> getAmountOfCommentsOnPost(@PathVariable Long post_id){
        List<Comment> comments = interactionService.getCommentsFromPost(post_id);
        int amountOfComments = comments.size();

        if (comments.isEmpty()){
            return new ResponseEntity<>(amountOfComments, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(amountOfComments, HttpStatus.OK);
    }

    @GetMapping("/getRepliesFromComment/comment_id")
    public ResponseEntity<List<Comment>> getRepliesFromComment(@PathVariable Long comment_id){
        List<Comment> replies = interactionService.getRepliesFromComment(comment_id);

        if (replies.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @GetMapping("/getComments/{post_id}")
    public ResponseEntity<List<Comment>> getCommentsFromPost(@PathVariable Long post_id){
        List<Comment> comments = interactionService.getCommentsFromPost(post_id);
        if(comments != null){
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/userId/{user_id}/postId/{post_id}/comment")
    public ResponseEntity<HttpStatus> commentOnPost(@PathVariable Long user_id,
                                                    @PathVariable Long post_id,
                                                    @RequestBody String comment){
        interactionService.commentOnPost(user_id, post_id, comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/userId/{user_id}/commentId/{comment_id}/comment")
    public ResponseEntity<HttpStatus> replyToComment(@PathVariable Long user_id,
                                                     @PathVariable Long comment_id,
                                                     @RequestBody String comment){
        interactionService.replyToComment(user_id, comment_id, comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/userId/{user_id}/commentId/{comment_id}/deleteComment")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long user_id, @PathVariable Long comment_id){
        Boolean trueIfCommentDeleted = interactionService.deleteComment(user_id, comment_id);
        if (trueIfCommentDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


