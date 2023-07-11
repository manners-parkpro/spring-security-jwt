package com.practice.growth.controller.setting;

import com.practice.growth.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/setting/admin-role-manager")
public class AdminRoleManagerController {

    private final AccountService service;

    @RequestMapping()
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("setting/admin-role-manager");
        return modelAndView;
    }
}
