package com.example.communityservice.services;

import com.example.communityservice.clients.UserClient;
import com.example.communityservice.dto.CommunityResponseDTO;
import com.example.communityservice.dto.UserResponseDTO;
import com.example.communityservice.model.CommunityUser;
import com.example.communityservice.model.Community;
import com.example.communityservice.repositories.CommunityRepository;
import com.example.communityservice.repositories.CommunityUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityUserRepository communityUserRepository;
    private final UserClient userClient;
    public Community createCommunity(Community newCommunity) {
        if (newCommunity.getName() == null || newCommunity.getName().isBlank()) {
            throw new IllegalArgumentException("Community name cannot be null or blank.");
        }

        if (communityRepository.existsByName(newCommunity.getName())) {
            throw new RuntimeException("Community already exists with name: " + newCommunity.getName());
        }

        if (newCommunity.getIsPrivate() == null) {
            newCommunity.setIsPrivate(false);
        }

        CommunityUser communityUser = new CommunityUser();
        communityUser.setUserId(newCommunity.getCreatedBy().getUserId());
        communityUser.setCommunityId(1L);//temp community id, gets changed after communityUser is saved
        communityUserRepository.save(communityUser);


        newCommunity.setCreatedBy(communityUser);
        communityRepository.save(newCommunity);

        communityUser.setCommunityId(newCommunity.getId());
        UserResponseDTO userGottenFromUserService = userClient.getUserFromUserService(communityUser.getUserId());
        communityUser.setUsername(userGottenFromUserService.getUsername());
        communityUserRepository.save(communityUser);

        if (newCommunity.getModerators() == null) {
            newCommunity.setModerators(new ArrayList<>());
        }

        if (newCommunity.getModerators().isEmpty()) {
            newCommunity.getModerators().add(newCommunity.getCreatedBy());
        }

        communityRepository.save(newCommunity);

        return newCommunity;
    }


    public List<CommunityResponseDTO> getCommunities(){
        return communityRepository.findAll()
                .stream()
                .map(this::convertCommunityToDTO)
                .collect(Collectors.toList());
    }

    public void deleteCommunityById(Long id){
        communityRepository.deleteById(id);
    }

    public Optional<Community> getCommunityById(Long id) {
        return communityRepository.findById(id);
    }

    public Optional<Community> getCommunityByName(String communityName) {
        return communityRepository.findByName(communityName);
    }

    public void updateCommunity(Community community) {
        communityRepository.save(community);
    }

    public void joinCommunity(Long community_id, Long user_id){

        //if user exists, add the user to the community
        UserResponseDTO user = userClient.getUserFromUserService(user_id);

        if (user != null) {
            Community community = communityRepository.findById(community_id)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            Optional<CommunityUser> communityMember = communityUserRepository.findByUserIdAndCommunityId(user_id, community_id);

            if(communityMember.isPresent()){
                throw new RuntimeException("member already exists in community");
            }

            CommunityUser newCommunityMember = new CommunityUser();
            newCommunityMember.setUserId(user_id);
            newCommunityMember.setCommunityId(community_id);
            communityUserRepository.save(newCommunityMember);

            //add the name of the user and save again
            UserResponseDTO userGottenFromUserService = userClient.getUserFromUserService(newCommunityMember.getId());
            newCommunityMember.setUsername(userGottenFromUserService.getUsername());
            communityUserRepository.save(newCommunityMember);

            community.getMembers().add(newCommunityMember);
            communityRepository.save(community);
        } else {
            throw new RuntimeException("an error occurred, user doesnt exist");
        }
    }

    public void leaveCommunity(Long community_id, Long user_id){

        //if user exists, add the user to the community
        UserResponseDTO user = userClient.getUserFromUserService(user_id);

        if (user != null) {
            Community community = communityRepository.findById(community_id)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            CommunityUser communityMember = communityUserRepository.findByUserIdAndCommunityId(user_id, community_id)
                    .orElseThrow(() -> new RuntimeException("CommunityMember not found"));

            community.getMembers().remove(communityMember);
            communityRepository.save(community);

            communityUserRepository.delete(communityMember);
        } else {
            throw new RuntimeException("an error occurred, user doesnt exist");
        }
    }

    public List<CommunityUser> getAllCommunityMembers(Long id){
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        List<CommunityUser> communityMemberList = community.getMembers();

        if (communityMemberList.isEmpty()){
            throw new RuntimeException("no members in this community");
        }

        return community.getMembers();
    }

    public Optional<CommunityUser> getCommunityMemberByUserId(Long user_id, Long community_id) {

        //if user exists, add the user to the community
        UserResponseDTO user = userClient.getUserFromUserService(user_id);

        if (user != null) {
            return communityUserRepository.findByUserIdAndCommunityId(user_id, community_id);
        } else {
            throw new RuntimeException("an error occurred, user doesnt exist");
        }
    }

    public List<CommunityResponseDTO> getAllCommunitiesByUserId(Long user_id) {

        //if user exists, add the user to the community
        UserResponseDTO user = userClient.getUserFromUserService(user_id);

        if (user != null) {
            List<CommunityUser> communityMemberList = communityUserRepository.findByUserId(user_id)
                    .orElseThrow(() -> new RuntimeException("no communityMembers found with user_id: " + user_id));

            List<CommunityResponseDTO> communityResponseDTOList = new ArrayList<>();

            for (int i = 0; i < communityMemberList.size(); i++){
                Long community_id = communityMemberList.get(i).getCommunityId();
                Community community = communityRepository.findById(community_id)
                        .orElseThrow(() -> new RuntimeException("Community not found"));

                CommunityResponseDTO communityResponseDTO = convertCommunityToDTO(community);

                communityResponseDTOList.add(communityResponseDTO);
            }

            return communityResponseDTOList;
        } else {
            throw new RuntimeException("an error occurred, user doesnt exist");
        }
    }

    public CommunityResponseDTO convertCommunityToDTO(Community community) {
        return new CommunityResponseDTO(
                community.getId(),
                community.getName(),
                community.getIsPrivate()
        );
    }
}
