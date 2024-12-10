package com.example.communityservice.controllers;

import com.example.communityservice.model.Rule;
import com.example.communityservice.services.RuleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/rules")
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<List<Rule>> getAllRules(){
        List<Rule> ruleList = ruleService.getAllRules();
        return new ResponseEntity<>(ruleList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Rule>> getRuleById(@PathVariable Long id){
        Optional<Rule> rule = ruleService.getRuleById(id);
        if (rule.isPresent()){
            return new ResponseEntity<>(rule, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PostMapping("/create")
    public ResponseEntity<Rule> createRule(@RequestBody Rule rule) {
        Rule newRule = ruleService.createCommunityRule(rule);
        return new ResponseEntity<>(newRule, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteRule(@PathVariable Long id){
        ruleService.deleteRuleById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/addRuleToCommunity/{community_id}/{rule_id}")
    public ResponseEntity<String> addRuleToCommunity(@PathVariable Long community_id, @PathVariable Long rule_id){
        ruleService.addRuleToCommunity(community_id, rule_id);
        return new ResponseEntity<>("rule successfully added to community with id: " + community_id, HttpStatus.OK);
    }

    @PostMapping("/removeRuleFromCommunity/{community_id}/{rule_id}")
    public ResponseEntity<String> removeRuleFromCommunity(@PathVariable Long community_id, @PathVariable Long rule_id){
        ruleService.removeRuleFromCommunity(community_id, rule_id);
        return new ResponseEntity<>("rule removed from community with id: " + community_id, HttpStatus.OK);
    }

}
