package org.dromara.system.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.mybatisflex.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysRoleMenu;

import java.util.List;

/**
 * 角色与菜单关联表 数据层
 *
 * @author Lion Li
 */
public interface SysRoleMenuMapper extends BaseMapperPlus<SysRoleMenu, SysRoleMenu> {

    /**
     * 根据菜单ID串删除关联关系
     *
     * @param menuIds 菜单ID串
     * @return 结果
     */
    default int deleteByMenuIds(List<Long> menuIds) {
        return this.deleteByQuery(QueryWrapper.create().in(SysRoleMenu::getMenuId, menuIds));
    }

}
