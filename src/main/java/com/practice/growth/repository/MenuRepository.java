package com.practice.growth.repository;

import com.practice.growth.domain.entity.Menu;
import com.practice.growth.domain.types.MenuType;
import com.practice.growth.domain.types.YNType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByMenuTypeAndDeleteYnAndParentIsNull(MenuType menuType, YNType deleteYn, Sort sort);

    /**
     * 불필요 권한을 제외한 모든 메뉴 갖고오기
     * @param role
     * @param menuType
     * @return
     */
    @Query("SELECT m FROM Menu m WHERE NOT EXISTS (SELECT r FROM m.roles r WHERE r.roleName=:role) and m.parent is null and m.menuType=:menuType " +
            "and m.activeYn='Y' " +
            "and m.deleteYn='N' ")
    List<Menu> getTopMenusNotExistsRole(@Param("role") String role, @Param("menuType") MenuType menuType, Sort sort);

    List<Menu> findByParentIsNullAndRoles_RoleName(String role);

    @Query("select distinct m From Menu m join m.roles r " +
            "where m.menuType=:menuType and m.parent is null " +
            "and m.activeYn='Y' " +
            "and m.displayYn='Y' " +
            "and m.deleteYn='N' " +
            "and r.roleName in :roles")
    List<Menu> getTopMenu(@Param("menuType") MenuType menuType, @Param("roles") List<String> roles, Sort sort);

    Menu findByMenuNameIgnoreCaseAndActiveYn(@Param("menuName") String menuName, YNType ynType);
}
