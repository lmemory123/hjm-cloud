package org.dromara.system.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.utils.StreamUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysMenu;
import org.dromara.system.domain.vo.SysMenuVo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单表 数据层
 *
 * @author Lion Li
 */
public interface SysMenuMapper extends BaseMapperPlus<SysMenu, SysMenuVo> {

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    default Set<String> selectMenuPermsByUserId(Long userId) {
        QueryWrapper roleIds = QueryWrapper.create()
            .select("sur.role_id")
            .from("sys_user_role sur")
            .leftJoin("sys_role sr").on("sr.role_id = sur.role_id")
            .where("sur.user_id = ? and sr.status = '0'", userId);
        QueryWrapper menuIds = QueryWrapper.create()
            .select("menu_id")
            .from("sys_role_menu")
            .in("role_id", roleIds);
        List<String> list = this.selectObjs(QueryWrapper.create()
            .select(SysMenu::getPerms)
            .in(SysMenu::getMenuId, menuIds)
            .isNotNull(SysMenu::getPerms), obj -> (String) obj);
        return new HashSet<>(StreamUtils.filter(list, StringUtils::isNotBlank));
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    default Set<String> selectMenuPermsByRoleId(Long roleId) {
        QueryWrapper menuIds = QueryWrapper.create()
            .select("srm.menu_id")
            .from("sys_role_menu srm")
            .leftJoin("sys_role sr").on("sr.role_id = srm.role_id")
            .where("srm.role_id = ? and sr.status = '0'", roleId);
        List<String> list = this.selectObjs(QueryWrapper.create()
            .select(SysMenu::getPerms)
            .in(SysMenu::getMenuId, menuIds)
            .isNotNull(SysMenu::getPerms), obj -> (String) obj);
        return new HashSet<>(StreamUtils.filter(list, StringUtils::isNotBlank));
    }

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    default List<SysMenu> selectMenuTreeAll() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .in(SysMenu::getMenuType, SystemConstants.TYPE_DIR, SystemConstants.TYPE_MENU)
            .eq(SysMenu::getStatus, SystemConstants.NORMAL)
            .orderBy(SysMenu::getParentId, true)
            .orderBy(SysMenu::getOrderNum, true);
        return this.selectListByQuery(queryWrapper);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId            角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    default List<Long> selectMenuListByRoleId(Long roleId, boolean menuCheckStrictly) {
        QueryWrapper menuIds = QueryWrapper.create()
            .select("srm.menu_id")
            .from("sys_role_menu srm")
            .leftJoin("sys_role sr").on("sr.role_id = srm.role_id")
            .where("srm.role_id = ? and sr.status = '0'", roleId);
        QueryWrapper wrapper = QueryWrapper.create()
            .select(SysMenu::getMenuId)
            .in(SysMenu::getMenuId, menuIds)
            .orderBy(SysMenu::getParentId, true)
            .orderBy(SysMenu::getOrderNum, true);
        if (menuCheckStrictly) {
            QueryWrapper parentIds = QueryWrapper.create()
                .select("parent_id")
                .from("sys_menu")
                .in("menu_id", menuIds);
            wrapper.notIn(SysMenu::getMenuId, parentIds);
        }
        return this.selectObjs(wrapper, obj -> (Long) obj);
    }

}
