package com.practice.growth.domain.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReserveStatusType {
    ABLE("able", "예약가능"),
    HOLD("hold", "대기"),
    DONE("done", "예약완료");

    final private String type;
    final private String description;
}
