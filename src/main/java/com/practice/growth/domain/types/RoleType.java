package com.practice.growth.domain.types;

import lombok.Getter;

@Getter
public enum RoleType {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_SYSTEM("ROLE_SYSTEM");

    private String desc;

    RoleType(String desc) {
        this.desc = desc;
    }
}
