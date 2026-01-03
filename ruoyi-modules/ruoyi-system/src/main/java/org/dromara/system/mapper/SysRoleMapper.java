package org.dromara.system.mapper;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.mybatisflex.annotation.DataColumn;
import org.dromara.common.mybatisflex.annotation.DataPermission;
import org.dromara.common.mybatisflex.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysRole;
import org.dromara.system.domain.vo.SysRoleVo;

import java.util.List;

/**
 * 角色表 数据层
 *
 * @author Lion Li
 */
public interface SysRoleMapper extends BaseMapperPlus<SysRole, SysRoleVo> {

    /**
     * 分页查询角色列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 包含角色信息的分页结果
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<SysRoleVo> selectPageRoleList(Page<SysRole> page, QueryWrapper queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 根据条件查询角色数据
     *
     * @param queryWrapper 查询条件
     * @return 角色数据集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default List<SysRoleVo> selectRoleList(QueryWrapper queryWrapper) {
        return this.selectVoList(queryWrapper);
    }

    /**
     * 根据角色ID集合查询角色数量
     *
     * @param roleIds 角色ID列表
     * @return 匹配的角色数量
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default long selectRoleCount(List<Long> roleIds) {
        return this.selectCountByQuery(QueryWrapper.create().in(SysRole::getRoleId, roleIds));
    }

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @return 对应的角色信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default SysRoleVo selectRoleById(Long roleId) {
        return this.selectVoById(roleId);
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    default List<SysRoleVo> selectRolesByUserId(Long userId) {
        QueryWrapper roleIds = QueryWrapper.create()
            .select("role_id")
            .from("sys_user_role")
            .where("user_id = ?", userId);
        return this.selectVoList(QueryWrapper.create()
            .select(SysRole::getRoleId, SysRole::getRoleName, SysRole::getRoleKey,
                SysRole::getRoleSort, SysRole::getDataScope, SysRole::getStatus)
            .in(SysRole::getRoleId, roleIds));
    }

}
