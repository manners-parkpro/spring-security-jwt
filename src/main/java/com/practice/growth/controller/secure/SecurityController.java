package com.practice.growth.controller.secure;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@Controller
@RequiredArgsConstructor
public class SecurityController {

    private final AccountService accountService;

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
