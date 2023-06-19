package com.practice.growth.controller;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account/")
public class ApiAccountController {

    private final AccountService service;

    @PostMapping("create-user")
    public ApiResult<Long> createUser(@RequestBody AccountDto dto) {
        ApiResult<Long> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            Long id = service.createUser(dto);
            result.setData(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setCode(ApiResult.RESULT_CODE_ERROR);
        }

        return result;
    }
}
