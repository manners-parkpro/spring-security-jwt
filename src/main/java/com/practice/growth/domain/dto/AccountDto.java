package com.practice.growth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String username;
    private String email;
    private String tel;
    private String prefixEmail;
    private String suffixEmail;
    private String password;
    private String role;
    private YNType activeYn;
    private YNType dormantYn;
    private YNType deleteYn;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime lastLoginAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public AccountDto(Account a) { this(a, false); }

    public AccountDto(Account a, boolean isPassword) {
        this.id = a.getId();
        this.username = a.getUsername();
        this.email = a.getEmail();

        if (a.getEmail() != null) {
            String[] emailArray = email.split("@");
            this.prefixEmail = emailArray[0];
            this.suffixEmail = emailArray[1];
        }

        this.tel = a.getTel();

        if (isPassword)
            this.password = a.getPassword();

        this.role = a.getRole();
        this.activeYn = a.getActiveYn();
        this.dormantYn = a.getDormantYn();
        this.deleteYn = a.getDeleteYn();
        this.lastLoginAt = a.getLastLoginAt();
        this.createdAt = a.getCreatedAt();
        this.updatedAt = a.getUpdatedAt();
    }
}
