package com.practice.growth.configurations.component;

import com.practice.growth.domain.types.MenuType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@Deprecated // =============================== 학습 필요 ===============================
public class GFFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource, InitializingBean {

    @Autowired
    UrlCache urlCache;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    private MenuType menuType;

    /**
     * 권한 설정이 안된 메뉴에 접근을 하고자 할 때 오류 발생
     */
    private final String NO_PERMIT = "ROLE_NO_PERMIT";
    private boolean disable = false;

    public GFFilterInvocationSecurityMetadataSource(MenuType menuType) {
        this.menuType = menuType;
    }

    /**
     * @param menuType
     * @param disable  true 일 경우 모든경로 ok
     */
    public GFFilterInvocationSecurityMetadataSource(MenuType menuType, boolean disable) {
        this(menuType);
        this.disable = disable;
    }

    @Override
    public void afterPropertiesSet() {
        if (menuType != MenuType.OAuth2)
            urlCache.reload(menuType);
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        Collection<ConfigAttribute> result = null;
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : urlCache.entrySet()) {
            if (entry.getKey().matches(request)) {
                result = entry.getValue();
                break;
            }
        }
        if (result == null && !"/login".equals(request.getRequestURI()) || disable) {
            //context-path 사용시 경로 삭제후 검사
            if (((FilterChainProxy) springSecurityFilterChain).getFilters(request.getRequestURI().replace(request.getContextPath(), "")).size() > 0) {
                result = SecurityConfig.createList(NO_PERMIT);
            }
        }
        return result;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        if (urlCache == null) return null;

        Set<ConfigAttribute> allAttribute = new HashSet<>();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : urlCache.entrySet()) {
            allAttribute.addAll(entry.getValue());
        }
        return allAttribute;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);

    }
}