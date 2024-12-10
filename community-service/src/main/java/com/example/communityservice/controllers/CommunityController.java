package com.example.communityservice.controllers;

import com.example.communityservice.dto.CommunityResponseDTO;
import com.example.communityservice.model.CommunityUser;
import com.example.communityservice.model.Community;
import com.example.communityservice.services.CommunityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/communities")
public class CommunityController {

    private final CommunityService communityService;

    //get all communities
    @GetMapping()
    public ResponseEntity<List<CommunityResponseDTO>> getCommunities(){
        try {
            List<CommunityResponseDTO> communityResponseDTOList = communityService.getCommunities();
            if (communityResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(communityResponseDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get a community by community_id
    @GetMapping("/id/{id}")
    public ResponseEntity<Community> getCommunityById(@PathVariable Long id){
        Optional<Community> community = communityService.getCommunityById(id);
        if (community.isPresent()){
            return new ResponseEntity<>(community.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //get a community by community_name
    @GetMapping("/name/{community_name}")
    public ResponseEntity<Community> getCommunityByName(@PathVariable String community_name){
        Optional<Community> community = communityService.getCommunityByName(community_name);
        if (community.isPresent()){
            return new ResponseEntity<>(community.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //create a new community
    @PostMapping("/create")
    public ResponseEntity<?> createCommunity(@RequestBody Community community){
        try {
            Community communityObj = communityService.createCommunity(community);
            CommunityResponseDTO communityResponseDTO = communityService.convertCommunityToDTO(communityObj);
            return new ResponseEntity<>(communityResponseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //update an existing community
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateCommunityById(@PathVariable Long id, @RequestBody Community newCommunityData){
        Optional<Community> oldCommunityObj = communityService.getCommunityById(id);

        if(oldCommunityObj.isPresent()){
            Community updatedCommunityData = oldCommunityObj.get();
            updatedCommunityData.setName(newCommunityData.getName());
            updatedCommunityData.setDescription(newCommunityData.getDescription());
            updatedCommunityData.setCreatedBy(newCommunityData.getCreatedBy());
            updatedCommunityData.setIsPrivate(newCommunityData.getIsPrivate());

            communityService.updateCommunity(updatedCommunityData);
            return new ResponseEntity<>(updatedCommunityData, HttpStatus.OK);
        }

        if (oldCommunityObj.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //delete a community
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteCommunity(@PathVariable Long id){
        communityService.deleteCommunityById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //join a community
    @PostMapping("/join/community/{community_id}/user/{user_id}")
    public ResponseEntity<String> joinCommunity(@PathVariable Long community_id, @PathVariable Long user_id){
        communityService.joinCommunity(community_id, user_id);
        return new ResponseEntity<>("successfully joined community with id: " + community_id, HttpStatus.OK);
    }

    //leave a community
    @PostMapping("/leave/community/{community_id}/user/{user_id}")
    public ResponseEntity<String> leaveCommunity(@PathVariable Long community_id, @PathVariable Long user_id){
        communityService.leaveCommunity(community_id, user_id);
        return new ResponseEntity<>("successfully left community with id: " + community_id, HttpStatus.OK);
    }

    //get all community members from a community by community_id
    @GetMapping("/members/{community_id}")
    public ResponseEntity<List<CommunityUser>> getAllCommunityMembers(@PathVariable Long community_id){
        List<CommunityUser> communityMemberSet = communityService.getAllCommunityMembers(community_id);
        return new ResponseEntity<>(communityMemberSet, HttpStatus.OK);
    }

    //get all joined communities from a user by user_id
    @GetMapping("user/{user_id}")
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunitiesByUserId(@PathVariable Long user_id){
        List<CommunityResponseDTO> communityResponseDTOList = communityService.getAllCommunitiesByUserId(user_id);
        if (communityResponseDTOList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(communityResponseDTOList, HttpStatus.OK);
    }

    //get a community member from a community
    @GetMapping("/user/{user_id}/community/{community_id}")
    public ResponseEntity<Optional<CommunityUser>> getCommunityMemberById(@PathVariable Long user_id, @PathVariable Long community_id){
        Optional<CommunityUser> communityMember = communityService.getCommunityMemberByUserId(user_id, community_id);
        if (communityMember.isPresent()){
            return new ResponseEntity<>(communityMember, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
