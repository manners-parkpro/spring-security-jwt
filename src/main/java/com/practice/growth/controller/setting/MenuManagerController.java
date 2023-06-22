package com.practice.growth.controller.setting;

import com.practice.growth.configurations.UrlCache;
import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.service.MenuService;
import com.practice.growth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/setting/menu-manager")
public class MenuManagerController {

    private final String DEFAULT_AC_ROLE = "ROLE_ADMIN";
    private final String DEFAULT_FE_ROLE = "ROLE_ANONYMOUS";

    private final MenuService menuService;
    private final RoleService roleService;
    private final UrlCache urlCache;

    @RequestMapping
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("setting/menu-manager");

        List<MenuDto> acMenus = menuService.getAllMenuHierarchy(MenuType.AdminConsole);
        List<MenuDto> feMenus = menuService.getAllMenuHierarchy(MenuType.FrontEnd);
        List<Role> roles = roleService.roleList();
        modelAndView.addObject("acMenus", acMenus);//admin console
        modelAndView.addObject("feMenus", feMenus);//front end
        modelAndView.addObject("roles", roles);

        return modelAndView;
    }
}
