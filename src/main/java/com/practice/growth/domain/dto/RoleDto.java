package com.practice.growth.domain.dto;

import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {
    private String roleName;
    private AccountDto account;
    private String description;
    /**
     * 삭제 가능 여부
     */
    private YNType defaultYn;

    public RoleDto(Role r) {
        this.roleName = r.getRoleName();
        this.description = r.getDescription();
        this.defaultYn = r.getDefaultYn();
        this.account = new AccountDto(r.getAccount());
    }

    public RoleDto(String roleName) {
        this.roleName = roleName;
    }
}
