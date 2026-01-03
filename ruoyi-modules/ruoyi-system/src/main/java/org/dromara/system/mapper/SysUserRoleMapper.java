package org.dromara.system.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.mybatisflex.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysUserRole;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author Lion Li
 */
public interface SysUserRoleMapper extends BaseMapperPlus<SysUserRole, SysUserRole> {

    /**
     * 根据角色ID查询关联的用户ID列表
     *
     * @param roleId 角色ID
     * @return 关联到指定角色的用户ID列表
     */
    default List<Long> selectUserIdsByRoleId(Long roleId) {
        return this.selectObjs(
            QueryWrapper.create()
                .select(SysUserRole::getUserId)
                .eq(SysUserRole::getRoleId, roleId),
            obj -> (Long) obj
        );
    }

}
