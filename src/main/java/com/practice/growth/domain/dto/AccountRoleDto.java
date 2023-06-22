package com.practice.growth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.AccountRole;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AccountRoleDto {

    private Long Id;
    private String roleName;
    private AccountDto account;
    private int sortOrder;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public AccountRoleDto(AccountRole r) { this(r, false); }

    public AccountRoleDto(AccountRole r, boolean isRequireAccount) {
        this.roleName = r.getRoleName();
        this.sortOrder = r.getSortOrder();

        if (isRequireAccount)
            this.account = new AccountDto(r.getAccount());
    }
}
