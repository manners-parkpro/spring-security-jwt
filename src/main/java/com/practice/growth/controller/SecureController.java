package com.practice.growth.controller;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.domain.dto.JwtTokenDto;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.provider.CustomAuthenticationProvider;
import com.practice.growth.provider.JwtProvider;
import com.practice.growth.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@RequiredArgsConstructor
public class SecureController {

    private final AccountService accountService;

    @GetMapping("login")
    public String login() {
        return "secure/login";
    }

    @GetMapping("user")
    public @ResponseBody String user() {
        return "User";
    }

    @GetMapping("/secure/register")
    public String register() {
        return "secure/register";
    }

    @PostMapping("/secure/create-user")
    public @ResponseBody ApiResult<Long> createUser(@RequestBody AccountDto dto) {
        ApiResult<Long> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            Long id = accountService.createUser(dto);
            result.setData(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setCode(ApiResult.RESULT_CODE_ERROR);
            result.setMessage(e.getMessage());
        }

        return result;
    }
}
