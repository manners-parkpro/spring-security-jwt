package com.practice.growth.controller.setting;

import com.practice.growth.configurations.component.UrlCache;
import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.service.MenuService;
import com.practice.growth.service.RoleService;
import com.practice.growth.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/setting/menu-manager")
public class MenuManagerController {

    private final String DEFAULT_AC_ROLE = "ROLE_ADMIN";
    private final String DEFAULT_FE_ROLE = "ROLE_USER";

    private final MenuService service;
    private final RoleService roleService;
    private final UrlCache urlCache;

    private final SecurityService securityService;

    @GetMapping("old")
    public ModelAndView old() {
        ModelAndView modelAndView = new ModelAndView("setting/menu-manager");

        List<MenuDto> acMenus = service.getAllMenuHierarchy(MenuType.AdminConsole);
        List<MenuDto> feMenus = service.getAllMenuHierarchy(MenuType.FrontEnd);
        List<Role> roles = roleService.roleList();
        modelAndView.addObject("acMenus", acMenus);//admin console
        modelAndView.addObject("feMenus", feMenus);//front end
        modelAndView.addObject("roles", roles);

        return modelAndView;
    }

    @RequestMapping()
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("setting/new-menu-manager");
        modelAndView.addObject("acMenus", service.getAllMenuHierarchy(MenuType.AdminConsole)); //admin console
        modelAndView.addObject("roles", roleService.roleList());
        return modelAndView;
    }

    @GetMapping("ajax/menu/{id}")
    @ResponseBody
    public ApiResult<MenuDto> getAjaxMenu(@PathVariable(name = "id") Long id) {
        try {
            Menu menu = service.getMenu(id);
            MenuDto dto = new MenuDto(menu);
            ApiResult<MenuDto> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);
            result.setData(dto);
            return result;
        } catch (NotFoundException e) {
            ApiResult<MenuDto> result = new ApiResult<>(ApiResult.RESULT_CODE_NOT_FOUND);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    @RequestMapping("ajax/menu/save")
    @ResponseBody
    public ApiResult<Long> save(@RequestBody MenuDto dto) throws NotFoundException {
        ApiResult<Long> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            Long id = service.createOrModifyMenu(dto, securityService.getLoginUser());
            result.setData(id);
        } catch (NotFoundException e) {
            result.setCode(ApiResult.RESULT_CODE_NOT_FOUND);
            result.setMessage("부모 Menu명을 다시한번 확인해주세요.");
        }

        return result;
    }
}
