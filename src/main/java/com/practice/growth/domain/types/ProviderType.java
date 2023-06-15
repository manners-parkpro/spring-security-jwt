package com.practice.growth.domain.types;

import lombok.Getter;

@Getter
public enum ProviderType {
    WEB("WEB"),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    NAVER("naver");

    private String desc;

    ProviderType(String desc) {
        this.desc = desc;
    }
}
