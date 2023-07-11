package com.practice.growth.service;

import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.entity.Notice;
import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.practice.growth.domain.types.AntMatcherType.All;
import static com.practice.growth.domain.types.AntMatcherType.Single;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.by;

@Log4j2
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository repository;
    private final RoleService roleService;

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

    /**
     * 권한 있는 메뉴 갖고 오기
     *
     * @param roles
     * @param includeNonActive 숨김 메뉴 표시 여부
     */
    public List<MenuDto> getMenuHierarchyByRoles(MenuType menuType, Collection<Role> roles, boolean includeNonActive) {
        List<Menu> topMenus = repository.getTopMenu(menuType, roles.stream().map(Role::getRoleName).collect(Collectors.toList()), by(Direction.ASC, "sortOrder", "id"));
        List<MenuDto> hierarchy = new ArrayList<>();
        for (Menu m : topMenus) {
            if (!includeNonActive && m.getActiveYn() == YNType.N) continue;
            hierarchy.add(new MenuDto(m, roles, true));
        }

        return hierarchy;
    }

    /**
     * 하나의 메뉴 갖고 오기
     *
     * @param menuId 메뉴 id
     * @return
     */
    public Menu getMenu(long menuId) throws NotFoundException {
        Optional<Menu> optMenu = Optional.of(repository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu not found", NotFoundException.MENU_NOT_FOUND)));
        return optMenu.get();
    }

    public Long createOrModifyMenu(MenuDto dto, Account writer) throws NotFoundException {
        Menu menu;

        if (dto.getId() != null) {
            Optional<Menu> optBoard = repository.findById(dto.getId());
            if (optBoard.isEmpty())
                throw new NotFoundException("Menu not found", NotFoundException.MENU_NOT_FOUND);

            menu = optBoard.get();
        } else
            menu = new Menu();

        assert dto.getMenuName() != null && dto.getUrl() != null;

        Menu parentsMenu = null;
        if (All.equals(dto.getAntMatcherType())) {
            parentsMenu = repository.findByMenuNameIgnoreCaseAndActiveYn(dto.getParentsMenuName(), YNType.Y);
            if (parentsMenu == null)
                throw new NotFoundException("Parents Menu not found", NotFoundException.MENU_NOT_FOUND);
        }

        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(MenuType.AdminConsole);
        menu.setUrl(dto.getUrl());
        menu.setAntMatcherType(dto.getAntMatcherType());
        menu.setSortOrder(dto.getSortOrder());
        menu.setLastModifier(writer);
        menu.setParent(parentsMenu);

        repository.save(menu);

        return menu.getId();
    }
}
