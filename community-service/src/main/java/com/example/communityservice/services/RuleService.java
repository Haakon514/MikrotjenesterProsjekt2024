package com.example.communityservice.services;

import com.example.communityservice.model.Community;
import com.example.communityservice.model.Rule;
import com.example.communityservice.repositories.CommunityRepository;
import com.example.communityservice.repositories.RuleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;
    private final CommunityRepository communityRepository;

    public List<Rule> getAllRules(){
        return ruleRepository.findAll();
    }

    public Optional<Rule> getRuleById(Long id){
        return ruleRepository.findById(id);
    }

    public Rule createCommunityRule(Rule rule){
        return ruleRepository.save(rule);
    }

    public void addRuleToCommunity(Long community_id, Long rule_id){
        Community community = communityRepository.findById(community_id)
                .orElseThrow(() -> new RuntimeException("Community not found"));


        Rule rule = ruleRepository.findById(rule_id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        if (community.getRules().contains(rule)) {
            throw new RuntimeException("Rule already associated with the community");
        }

        community.getRules().add(rule);
        communityRepository.save(community);

    }

    public void removeRuleFromCommunity(Long community_id, Long rule_id){

        Community community = communityRepository.findById(community_id)
                .orElseThrow(() -> new RuntimeException("Community not found"));


        Rule rule = ruleRepository.findById(rule_id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));


        if (community.getRules().contains(rule)) {
            community.getRules().remove(rule);
            communityRepository.save(community);
        } else {
            throw new RuntimeException("Rule not associated with the community");
        }
    }

    public void deleteRuleById(Long id) {
        ruleRepository.deleteById(id);
    }
}
