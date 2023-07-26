package com.practice.growth.service;

import com.practice.growth.domain.dto.AccountDto;
import com.practice.growth.domain.dto.MenuDto;
import com.practice.growth.domain.dto.RoleDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.entity.Notice;
import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.domain.types.ModifyType;
import com.practice.growth.domain.types.RoleType;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.repository.MenuRepository;
import com.practice.growth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<MenuDto> getAll(MenuType menuType) {
        return repository.findByMenuTypeAndActiveYnAndDeleteYn(menuType, YNType.Y, YNType.N, by(Direction.ASC, "id")).stream().map(m -> new MenuDto(m, true)).collect(Collectors.toList());
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

        if (!CollectionUtils.isEmpty(topMenus)) {
            for (Menu m : topMenus) {
                if (!includeNonActive && m.getActiveYn() == YNType.N) continue;
                hierarchy.add(new MenuDto(m, roles, true));
            }
        }

        return hierarchy;
    }

    public List<MenuDto> getMenuHierarchyByRoleName(String roleName) throws NotFoundException {
        Role role = roleService.findByRoleName(roleName);
        if (role == null)
            throw new NotFoundException("Role not found", NotFoundException.ROLE_NOT_FOUND);

        List<MenuDto> hierarchy = new ArrayList<>();
        if (!CollectionUtils.isEmpty(role.getMenus()))
            role.getMenus().stream().forEach(m -> hierarchy.add(new MenuDto(m)));

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

    @Transactional
    public void createOrModifyMenuRole(MenuDto dto) throws NotFoundException {
        if (dto == null)
            throw new NotFoundException("Not found", NotFoundException.MENU_NOT_FOUND);

        Role role = roleService.findByRoleName(dto.getRoles().get(0).getRoleName());
        if (role == null)

        role.getMenus().clear();

        Menu menu = null;

        // Role 삭제
        if (!CollectionUtils.isEmpty(Arrays.asList(dto.getIdxs()))) {
            for (Long idx : dto.getIdxs()) {
                menu = getMenu(idx);
                menu.getRoles().remove(role);
                role.getMenus().remove(menu);
            }
        }

        // Role 재등록
        if (!CollectionUtils.isEmpty(Arrays.asList(dto.getIds()))) {
            for (Long id : dto.getIds()) {
                menu = getMenu(id);
                menu.getRoles().add(role);
                role.getMenus().add(menu);
            }
        }

        repository.save(menu);
    }

    @Transactional
    public void allChangeMenuRole(long menuId, List<RoleDto> roles) throws NotFoundException {
        Menu menu = getMenu(menuId);
        menu.setRoles(new HashSet<>());

        for (RoleDto dto : roles) {
            Role role = roleService.findByRoleName(dto.getRoleName());
            allModifyChildrenMenu(menu, role, ModifyType.Register);
        }

        repository.save(menu);
    }

    @Transactional
    public void allModifyChildrenMenu(Menu menu, Role role, ModifyType modifyType) throws NotFoundException {
        if (modifyType == ModifyType.Register) {
            menu.getRoles().add(role);
            role.getMenus().add(menu);
        } else {
            menu.getRoles().remove(role);
            role.getMenus().remove(menu);
        }

        if (!CollectionUtils.isEmpty(menu.getChildren())) {
            for (Menu m : menu.getChildren()) {
                allModifyChildrenMenu(m, role, modifyType);
            }
        }
    }
}
