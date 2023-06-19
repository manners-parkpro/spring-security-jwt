package com.practice.growth.controller;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/secure/")
public class SecureController {

    private final AccountService accountService;

    @GetMapping("login")
    public String login() {
        return "secure/login";
    }

    @GetMapping("register")
    public String register() {
        return "secure/register";
    }

    @PostMapping("create-user")
    public String createUser(AccountDto dto) {
        ApiResult<Long> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            Long id = accountService.createUser(dto);
            result.setData(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setCode(ApiResult.RESULT_CODE_ERROR);
        }

        return "redirect:/secure/login";
    }
}
