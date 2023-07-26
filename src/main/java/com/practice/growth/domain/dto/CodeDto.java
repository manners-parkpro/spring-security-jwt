package com.practice.growth.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.growth.domain.entity.Code;
import com.practice.growth.domain.types.ModifyType;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * @author prographer
 * @date: 10/25/19
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
public class CodeDto {

    private Long id;

    /**
     * 코드
     */
    private String code;
    /**
     * 코드그룹
     */
    private CodeDto group;
    /**
     * 코드 명
     */
    private String description;
    /**
     * 코드 값
     */
    private String value;

    /**
     * 사용 여부
     */
    private YNType activeYn;

    private String codeType;

    private String modifyType;

    /**
     * 최종 수정자
     */
    @JsonIgnore
    private AccountDto lastModifier;
    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonIgnore
    private List<CodeDto> children = new ArrayList<>();

    public CodeDto(Code code) {
        this.id = code.getId();
        this.code = code.getCode();
        this.description = code.getDescription();
        this.value = code.getValue();
        this.activeYn = code.getActiveYn();
        this.lastModifier = new AccountDto(code.getLastModifier());
        this.createdAt = code.getCreatedAt();
        this.updatedAt = code.getUpdatedAt();
    }

    public CodeDto(Code code, boolean makeChildren) {
        this(code);
        if (makeChildren && code.getChildren() != null) {
            for (Code c : code.getChildren()) {
                this.children.add(new CodeDto(c, true));
            }
        }
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
