package com.practice.growth.domain.dto;

import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.entity.Role;
import com.practice.growth.domain.types.AntMatcherType;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MenuDto {

    private Long id;
    private String menuName;
    private String parentsMenuName;
    private String url;
    private String iconClass;
    private YNType activeYn;
    private YNType displayYn;
    private int sortOrder;
    private AntMatcherType antMatcherType;
    private MenuType menuType;
    private List<MenuDto> children = new ArrayList<>();
    private List<RoleDto> roles = new ArrayList<>();

    public MenuDto(Menu menu) {
        this.id = menu.getId();
        this.menuName = menu.getMenuName();
        this.activeYn = menu.getActiveYn();
        this.displayYn = menu.getDisplayYn();
        this.sortOrder = menu.getSortOrder();
        this.url = menu.getUrl();
        this.iconClass = menu.getIconClass();
        this.antMatcherType = menu.getAntMatcherType();
        this.roles = menu.getRoles().stream().map(RoleDto::new).collect(Collectors.toList());
        this.menuType = menu.getMenuType();
    }

    public MenuDto(Menu menu, boolean makeChildren) {
        this(menu);
        if (makeChildren && !CollectionUtils.isEmpty(menu.getChildren())) {
            for (Menu c : menu.getChildren()) {
                if (c.getDeleteYn() == YNType.N)
                    this.children.add(new MenuDto(c, true));
            }
        }
    }

    /**
     * @param menu
     * @param roles
     * @param checkActive 액티브 유무 확인
     */
    public MenuDto(Menu menu, Collection<Role> roles, boolean checkActive) {
        this(menu);
        if (!CollectionUtils.isEmpty(menu.getChildren())) {
            for (Menu c : menu.getChildren()) {
                if (checkActive && c.getActiveYn() == YNType.N) continue;
                if (c.getDisplayYn() == YNType.N) continue;

                long cnt = roles.stream().filter(r -> c.getRoles().contains(r)).count();
                if (cnt > 0)
                    this.children.add(new MenuDto(c, roles, checkActive));
            }
        }
    }
}
