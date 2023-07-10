package com.practice.growth.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseSearchDto {

    private int page = 0;
    private int size = 10;
    private String searchType = "ALL";
    private String searchTitle;
}
