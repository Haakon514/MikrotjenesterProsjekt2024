package com.example.communityservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommunityResponseDTO {

    private Long id;
    private String name;
    private Boolean isPrivate;

}
