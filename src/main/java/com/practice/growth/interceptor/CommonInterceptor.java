package com.practice.growth.interceptor;

import com.practice.growth.domain.types.MenuType;
import com.practice.growth.exception.RequiredParamNonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {

    private MenuType menuType;

    /**
     * 메뉴종류를 확인 하기 위한 필수값
     * @param menuType
     */
    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (menuType == null)
            throw new RequiredParamNonException("MenuType is null");

        if (modelAndView != null) {

        }

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
