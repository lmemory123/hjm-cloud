package org.dromara.system.mapper;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.mybatisflex.annotation.DataColumn;
import org.dromara.common.mybatisflex.annotation.DataPermission;
import org.dromara.common.mybatisflex.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.vo.SysUserExportVo;
import org.dromara.system.domain.vo.SysUserVo;

import java.util.List;

/**
 * 用户表 数据层
 *
 * @author Lion Li
 */
public interface SysUserMapper extends BaseMapperPlus<SysUser, SysUserVo> {

    /**
     * 分页查询用户列表，并进行数据权限控制
     *
     * @param page         分页参数
     * @param queryWrapper 查询条件
     * @return 分页的用户信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<SysUserVo> selectPageUserList(Page<SysUser> page, QueryWrapper queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 查询用户列表，并进行数据权限控制
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default List<SysUserVo> selectUserList(QueryWrapper queryWrapper) {
        return this.selectVoList(queryWrapper);
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.create_by")
    })
    default List<SysUserExportVo> selectUserExportList(QueryWrapper queryWrapper) {
        queryWrapper
            .select("u.user_id", "u.dept_id", "u.nick_name", "u.user_name", "u.email", "u.avatar", "u.phonenumber",
                "u.sex", "u.status", "u.del_flag", "u.login_ip", "u.login_date", "u.create_by", "u.create_time",
                "u.remark", "d.dept_name", "d.leader", "u1.user_name as leaderName")
            .from("sys_user u")
            .leftJoin("sys_dept d").on("u.dept_id = d.dept_id")
            .leftJoin("sys_user u1").on("u1.user_id = d.leader");
        return this.selectListByQueryAs(queryWrapper, SysUserExportVo.class);
    }

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param page         分页信息
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.create_by")
    })
    default Page<SysUserVo> selectAllocatedList(Page<SysUser> page, QueryWrapper queryWrapper) {
        queryWrapper
            .select("distinct u.user_id", "u.dept_id", "u.user_name", "u.nick_name", "u.email", "u.phonenumber",
                "u.status", "u.create_time")
            .from("sys_user u")
            .leftJoin("sys_dept d").on("u.dept_id = d.dept_id")
            .leftJoin("sys_user_role sur").on("u.user_id = sur.user_id")
            .leftJoin("sys_role r").on("r.role_id = sur.role_id");
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param queryWrapper 查询条件
     * @return 用户信息集合信息
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "d.dept_id"),
        @DataColumn(key = "userName", value = "u.create_by")
    })
    default Page<SysUserVo> selectUnallocatedList(Page<SysUser> page, QueryWrapper queryWrapper) {
        queryWrapper
            .select("distinct u.user_id", "u.dept_id", "u.user_name", "u.nick_name", "u.email", "u.phonenumber",
                "u.status", "u.create_time")
            .from("sys_user u")
            .leftJoin("sys_dept d").on("u.dept_id = d.dept_id")
            .leftJoin("sys_user_role sur").on("u.user_id = sur.user_id")
            .leftJoin("sys_role r").on("r.role_id = sur.role_id");
        return this.selectVoPage(page, queryWrapper);
    }

    /**
     * 根据用户ID统计用户数量
     *
     * @param userId 用户ID
     * @return 用户数量
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default long countUserById(Long userId) {
        return this.selectCountByQuery(QueryWrapper.create().eq(SysUser::getUserId, userId));
    }

    /**
     * 根据条件更新用户数据
     *
     * @param user          要更新的用户实体
     * @param updateWrapper 更新条件封装器
     * @return 更新操作影响的行数
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default int updateByQueryWithPermission(SysUser user, QueryWrapper updateWrapper) {
        return this.updateByQuery(user, updateWrapper);
    }

    /**
     * 根据用户ID更新用户数据
     *
     * @param user 要更新的用户实体
     * @return 更新操作影响的行数
     */
    @DataPermission({
        @DataColumn(key = "deptName", value = "dept_id"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default int updateById(SysUser user) {
        return this.update(user);
    }

}
