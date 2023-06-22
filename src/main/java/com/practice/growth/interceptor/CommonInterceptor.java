package com.practice.growth.interceptor;

import com.practice.growth.domain.types.MenuType;
import com.practice.growth.exception.RequiredParamNonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
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
        log.info("CommonInterceptor postHandle()");

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
