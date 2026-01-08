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

import static org.dromara.system.domain.table.SysMenuTableDef.SYS_MENU;
import static org.dromara.system.domain.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static org.dromara.system.domain.table.SysRoleTableDef.SYS_ROLE;
import static org.dromara.system.domain.table.SysUserRoleTableDef.SYS_USER_ROLE;

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
                .select(SYS_USER_ROLE.ROLE_ID)
                .from(SYS_USER_ROLE)
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId).and(SYS_ROLE.STATUS.eq("0")));
        QueryWrapper menuIds = QueryWrapper.create()
                .select(SYS_ROLE_MENU.MENU_ID)
                .from(SYS_ROLE_MENU)
                .where(SYS_ROLE_MENU.ROLE_ID.in(roleIds));
        List<String> list = this.selectListByQueryAs(QueryWrapper.create()
                .select(SYS_MENU.PERMS)
                .where(SYS_MENU.MENU_ID.in(menuIds)
                        .and(SYS_MENU.PERMS.isNotNull())
                ), String.class);
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
                .select(SYS_ROLE_MENU.MENU_ID)
                .from(SYS_ROLE_MENU)
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ROLE_ID.eq(SYS_ROLE_MENU.ROLE_ID))
                .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId).and(SYS_ROLE.STATUS.eq("0")));
        List<String> list = this.selectListByQueryAs(QueryWrapper.create()
                .select(SYS_MENU.PERMS)
                .where(SYS_MENU.MENU_ID.in(menuIds)
                        .and(SYS_MENU.PERMS.isNotNull())
                ), String.class);
        return new HashSet<>(StreamUtils.filter(list, StringUtils::isNotBlank));
    }

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    default List<SysMenu> selectMenuTreeAll() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(SYS_MENU.MENU_TYPE.in(SystemConstants.TYPE_DIR, SystemConstants.TYPE_MENU)
                        .and(SYS_MENU.STATUS.eq(SystemConstants.NORMAL))
                )
                .orderBy(SYS_MENU.PARENT_ID.asc())
                .orderBy(SYS_MENU.ORDER_NUM.asc());
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
                .select(SYS_ROLE_MENU.MENU_ID)
                .from(SYS_ROLE_MENU)
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ROLE_ID.eq(SYS_ROLE_MENU.ROLE_ID))
                .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId).and(SYS_ROLE.STATUS.eq("0")));
        QueryWrapper wrapper = QueryWrapper.create()
                .select(SYS_MENU.MENU_ID)
                .where(SYS_MENU.MENU_ID.in(menuIds))
                .orderBy(SYS_MENU.PARENT_ID.asc())
                .orderBy(SYS_MENU.ORDER_NUM.asc());
        if (menuCheckStrictly) {
            QueryWrapper parentIds = QueryWrapper.create()
                    .select(SYS_MENU.PARENT_ID)
                    .where(SYS_MENU.MENU_ID.in(menuIds));
            wrapper.and(SYS_MENU.MENU_ID.notIn(parentIds));
        }
        return this.selectListByQueryAs(wrapper, Long.class);
    }

}
