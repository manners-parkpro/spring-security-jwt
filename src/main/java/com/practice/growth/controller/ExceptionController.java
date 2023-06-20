package com.practice.growth.controller;

import com.practice.growth.exception.NotFoundException;
import com.practice.growth.exception.UserNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @RequestMapping(value = "/entrypoint")
    public void entrypointException() throws NotFoundException {
        throw new NotFoundException("Entrypoint NotFoundException");
    }

    @RequestMapping(value = "/accessdenied")
    public void accessdeniedException() {
        throw new AccessDeniedException("권한이 없습니다.");
    }
}