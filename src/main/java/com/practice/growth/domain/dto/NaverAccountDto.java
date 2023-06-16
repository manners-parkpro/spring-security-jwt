package com.practice.growth.domain.dto;

import com.practice.growth.domain.types.ProviderType;

import java.util.Map;

public class NaverAccountDto implements OAuthAccount {

    private Map<String, Object> attributes;

    public NaverAccountDto(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProviderId() {
        return ProviderType.NAVER.getDesc();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
