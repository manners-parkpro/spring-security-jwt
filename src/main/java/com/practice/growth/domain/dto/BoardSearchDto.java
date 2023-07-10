package com.practice.growth.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSearchDto extends BaseSearchDto {

    private String fixedTopYn = "ALL";
    private String activeYn = "ALL";
}
