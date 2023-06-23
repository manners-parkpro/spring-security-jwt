package com.practice.growth.interceptor;

import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.service.MenuService;
import com.practice.growth.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {

    private MenuType menuType;
    private final SecurityService securityService;
    private final MenuService menuService;

    /**
     * 메뉴종류를 확인 하기 위한 필수값
     * @param menuType
     */
    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        setMenuType(MenuType.AdminConsole);

        if (modelAndView != null) {
            Account loginAccount = securityService.getLoginUser();
            if (loginAccount != null) {
                List<MenuDto> menus = menuService.getMenuHierarchyByRoles(menuType, loginAccount.getRoles(), false);
                modelAndView.addObject("menus", menus);
            }
        }

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
