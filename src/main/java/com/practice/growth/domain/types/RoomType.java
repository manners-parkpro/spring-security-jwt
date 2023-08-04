package com.practice.growth.domain.types;

import lombok.Getter;

@Getter
public enum RoomType {

    STANDARD("standard"),
    SWEET("sweet"),
    DELUXE("deluxe");

    private String desc;

    RoomType(String desc) {
        this.desc = desc;
    }
}
