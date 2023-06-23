package com.practice.growth.configurations.component;

import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.types.AntMatcherType;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UrlCache extends LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> {

    private final MenuRepository menuRepository;

    /**
     * 권한이 변경될경우 호출해야 함.
     */
    public void reload(MenuType menuType) {
        Sort sort = Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("antMatcherType"));
        List<Menu> menus = menuRepository.getTopMenusNotExistsRole("ROLE_ANONYMOUS", menuType, sort);
        this.clear();
        putData(menus);
    }

    private void putData(List<Menu> menus) {
        for (Menu m : menus) {
            String url;
            url = m.getUrl();

            //하위 메뉴가 있을 경우는 무조건 싱글로 처리
            if (!CollectionUtils.isEmpty(m.getChildren()))
                url += "";
            else
                url += (m.getAntMatcherType() != AntMatcherType.Single ? "/**" : "");

            //삭제되거나 비활성화 된 메뉴에는 접근 불가 권한 넣기
            if (m.getActiveYn() == YNType.N)
                this.put(new AntPathRequestMatcher(url), Collections.singletonList(new SecurityConfig("ROLE_NO_PERMIT")));
            else
                this.put(new AntPathRequestMatcher(url), m.getRoles().stream().map(r -> new SecurityConfig(r.getRoleName())).collect(Collectors.toList()));

            if (!CollectionUtils.isEmpty(m.getChildren()))
                putData(m.getChildren());
        }
    }
}
