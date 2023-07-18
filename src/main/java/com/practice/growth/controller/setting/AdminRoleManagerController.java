package com.practice.growth.controller.setting;

import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.service.MenuService;
import com.practice.growth.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/setting/admin-role-manager")
public class AdminRoleManagerController {

    private final RoleService roleService;
    private final MenuService menuService;

    @RequestMapping()
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("setting/admin-role-manager");
        modelAndView.addObject("roles", roleService.roleList());
        modelAndView.addObject("allMenus", menuService.getAll(MenuType.AdminConsole));

        return modelAndView;
    }

    @GetMapping("ajax/role/{roleName}")
    public @ResponseBody ApiResult<List<MenuDto>> getAjaxAdminRole(@PathVariable String roleName) throws NotFoundException {
        ApiResult<List<MenuDto>> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);
        result.setData(menuService.getMenuHierarchyByRoleName(roleName));
        return result;
    }

    @PostMapping("ajax/save/role")
    public @ResponseBody ApiResult save(@RequestBody MenuDto dto) {
        ApiResult result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            menuService.createOrModifyMenuRole(dto);
        } catch (NotFoundException e) {
            result.setCode(ApiResult.RESULT_CODE_NOT_FOUND);
        }

        return result;
    }
}
