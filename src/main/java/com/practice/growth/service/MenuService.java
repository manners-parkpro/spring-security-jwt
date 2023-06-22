package com.practice.growth.service;

import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository repository;
    private final RoleService roleService;
    private final SecurityService securityService;

    /**
     * 메뉴 계층구조 갖고 오기
     *
     * @return
     */
    public List<MenuDto> getAllMenuHierarchy(MenuType menuType) {
        List<Menu> topMenus = repository.findByMenuTypeAndDeleteYnAndParentIsNull(menuType, YNType.N, by(Direction.ASC, "sortOrder", "id"));
        List<MenuDto> hierarchy = new ArrayList<>();

        topMenus.stream().forEach(m -> hierarchy.add(new MenuDto(m, true)));

        return hierarchy;
    }
}
