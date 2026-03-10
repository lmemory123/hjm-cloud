package org.dromara.system.service.impl;

import cn.hutool.v7.core.array.ArrayUtil;
import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.util.ObjUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.*;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.SysUserPost;
import org.dromara.system.domain.SysUserRole;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.vo.SysPostVo;
import org.dromara.system.domain.vo.SysRoleVo;
import org.dromara.system.domain.vo.SysUserExportVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.*;
import org.dromara.system.service.ISysUserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.dromara.system.domain.table.SysUserTableDef.SYS_USER;

/**
 * 用户 业务层处理
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper baseMapper;
    private final SysDeptMapper deptMapper;
    private final SysRoleMapper roleMapper;
    private final SysPostMapper postMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysUserPostMapper userPostMapper;

    @Override
    public TableDataInfo<SysUserVo> selectPageUserList(SysUserBo user, PageQuery pageQuery) {
        Page<SysUserVo> page = baseMapper.selectPageUserList(pageQuery.build(), this.buildQueryWrapper(user));
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUserExportVo> selectUserExportList(SysUserBo user) {
        Map<String, Object> params = user.getParams();
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("u.del_flag", SystemConstants.NORMAL)
            .like("u.user_name", user.getUserName(), StringUtils.isNotBlank(user.getUserName()))
            .like("u.nick_name", user.getNickName(), StringUtils.isNotBlank(user.getNickName()))
            .eq("u.status", user.getStatus(), StringUtils.isNotBlank(user.getStatus()))
            .like("u.phonenumber", user.getPhonenumber(), StringUtils.isNotBlank(user.getPhonenumber()))
            .between("u.create_time", params.get("beginTime"), params.get("endTime"),
                params.get("beginTime") != null && params.get("endTime") != null)
            .orderBy("u.user_id", true);
        if (ObjUtil.isNotNull(user.getDeptId())) {
            List<Long> deptIds = deptMapper.selectDeptAndChildById(user.getDeptId());
            wrapper.in(SysUser::getDeptId, deptIds);
        }
        return baseMapper.selectUserExportList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(SysUserBo bo) {
        Map<String, Object> params = bo.getParams();
        System.out.println("params = " + bo);
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SYS_USER.USER_ID.eq(bo.getUserId())
                .and(SYS_USER.USER_ID.in(StringUtils.splitTo(bo.getUserIds(), ConvertUtil::toLong), StringUtils.isNotBlank(bo.getUserIds())))
                .and(SYS_USER.USER_NAME.like(bo.getUserName()))
                .and(SYS_USER.NICK_NAME.like(bo.getNickName()))
                .and(SYS_USER.STATUS.eq(bo.getStatus()))
                .and(SYS_USER.PHONENUMBER.like(bo.getPhonenumber()))
            )
            .orderBy(SYS_USER.USER_ID.asc());
        // 处理日期范围
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            queryWrapper.and(SYS_USER.CREATE_TIME.between(params.get("beginTime"), params.get("endTime")));
        }
        // 处理部门筛选
        if (ObjUtil.isNotNull(bo.getDeptId())) {
            List<Long> ids = deptMapper.selectDeptAndChildById(bo.getDeptId());
            queryWrapper.and(SYS_USER.DEPT_ID.in(ids));
        }
        // 处理排除用户
        if (StringUtils.isNotBlank(bo.getExcludeUserIds())) {
            queryWrapper.and(SYS_USER.USER_ID.notIn(StringUtils.splitTo(bo.getExcludeUserIds(), ConvertUtil::toLong)));
        }
        return queryWrapper;
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUserVo> selectAllocatedList(SysUserBo user, PageQuery pageQuery) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("u.del_flag", SystemConstants.NORMAL)
            .eq("r.role_id", user.getRoleId(), ObjUtil.isNotNull(user.getRoleId()))
            .like("u.user_name", user.getUserName(), StringUtils.isNotBlank(user.getUserName()))
            .eq("u.status", user.getStatus(), StringUtils.isNotBlank(user.getStatus()))
            .like("u.phonenumber", user.getPhonenumber(), StringUtils.isNotBlank(user.getPhonenumber()))
            .orderBy("u.user_id", true);
        Page<SysUserVo> page = baseMapper.selectAllocatedList(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public TableDataInfo<SysUserVo> selectUnallocatedList(SysUserBo user, PageQuery pageQuery) {
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(user.getRoleId());
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq("u.del_flag", SystemConstants.NORMAL)
            .ne("r.role_id", user.getRoleId()).or("r.role_id.isNull")
            .notIn("u.user_id", userIds, CollUtil.isNotEmpty(userIds))
            .like("u.user_name", user.getUserName(), StringUtils.isNotBlank(user.getUserName()))
            .like("u.phonenumber", user.getPhonenumber(), StringUtils.isNotBlank(user.getPhonenumber()))
            .orderBy("u.user_id", true);
        Page<SysUserVo> page = baseMapper.selectUnallocatedList(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByUserName(String userName) {
        return baseMapper.selectVoOne(QueryWrapper.create().eq(SysUser::getUserName, userName));
    }

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserByPhonenumber(String phonenumber) {
        return baseMapper.selectVoOne(QueryWrapper.create().eq(SysUser::getPhonenumber, phonenumber));
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUserVo selectUserById(Long userId) {
        SysUserVo user = baseMapper.selectVoById(userId);
        if (ObjUtil.isNull(user)) {
            return user;
        }
        user.setRoles(roleMapper.selectRolesByUserId(user.getUserId()));
        return user;
    }

    /**
     * 通过用户ID串查询用户
     *
     * @param userIds 用户ID串
     * @param deptId  部门id
     * @return 用户列表信息
     */
    @Override
    public List<SysUserVo> selectUserByIds(List<Long> userIds, Long deptId) {
        return baseMapper.selectUserList(QueryWrapper.create()
            .select(SYS_USER.USER_ID, SYS_USER.USER_NAME, SYS_USER.NICK_NAME, SYS_USER.EMAIL, SYS_USER.PHONENUMBER)
            .where(
                SYS_USER.STATUS.eq(SystemConstants.NORMAL)
                    .and(SYS_USER.DEPT_ID.eq(deptId, ObjUtil.isNotNull(deptId)))
                    .and(SYS_USER.USER_ID.in(userIds, CollUtil.isNotEmpty(userIds))
                    )
            ));
    }

    /**
     * 查询用户所属角色组
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(Long userId) {
        List<SysRoleVo> list = roleMapper.selectRolesByUserId(userId);
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return StreamUtils.join(list, SysRoleVo::getRoleName);
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(Long userId) {
        List<SysPostVo> list = postMapper.selectPostsByUserId(userId);
        if (CollUtil.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return StreamUtils.join(list, SysPostVo::getPostName);
    }

    /**
     * 校验用户账号是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUserBo user) {
        boolean exist = baseMapper.selectCountByQuery(QueryWrapper.create()
            .eq(SysUser::getUserName, user.getUserName())
            .ne(SysUser::getUserId, user.getUserId(), ObjUtil.isNotNull(user.getUserId()))) > 0;
        return !exist;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     */
    @Override
    public boolean checkPhoneUnique(SysUserBo user) {
        boolean exist = baseMapper.selectCountByQuery(QueryWrapper.create()
            .eq(SysUser::getPhonenumber, user.getPhonenumber())
            .ne(SysUser::getUserId, user.getUserId(), ObjUtil.isNotNull(user.getUserId()))) > 0;
        return !exist;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     */
    @Override
    public boolean checkEmailUnique(SysUserBo user) {
        boolean exist = baseMapper.selectCountByQuery(QueryWrapper.create()
            .eq(SysUser::getEmail, user.getEmail())
            .ne(SysUser::getUserId, user.getUserId(), ObjUtil.isNotNull(user.getUserId()))) > 0;
        return !exist;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param userId 用户ID
     */
    @Override
    public void checkUserAllowed(Long userId) {
        if (ObjUtil.isNotNull(userId) && LoginHelper.isSuperAdmin(userId)) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (ObjUtil.isNull(userId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        if (baseMapper.countUserById(userId) == 0) {
            throw new ServiceException("没有权限访问用户数据！");
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUserBo user) {
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        // 新增用户信息
        int rows = baseMapper.insert(sysUser);
        user.setUserId(sysUser.getUserId());
        // 新增用户岗位关联
        insertUserPost(user, false);
        // 新增用户与角色管理
        insertUserRole(user, false);
        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUserBo user, String tenantId) {
        user.setCreateBy(0L);
        user.setUpdateBy(0L);
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        sysUser.setTenantId(tenantId);
        return baseMapper.insert(sysUser) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = CacheNames.SYS_NICKNAME, key = "#user.userId")
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUserBo user) {
        // 新增用户与角色管理
        insertUserRole(user, true);
        // 新增用户与岗位管理
        insertUserPost(user, true);
        SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
        // 防止错误更新后导致的数据误删除
        int flag = baseMapper.updateById(sysUser);
        if (flag < 1) {
            throw new ServiceException("修改用户{}信息失败", user.getUserName());
        }
        return flag;
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(Long userId, Long[] roleIds) {
        insertUserRole(userId, roleIds, true);
    }

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 帐号状态
     * @return 结果
     */
    @Override
    public int updateUserStatus(Long userId, String status) {
        SysUser update = new SysUser();
        update.setStatus(status);
        return baseMapper.updateByQueryWithPermission(update, QueryWrapper.create()
            .eq(SysUser::getUserId, userId));
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_NICKNAME, key = "#user.userId")
    @Override
    public int updateUserProfile(SysUserBo user) {
        SysUser update = new SysUser();
        if (ObjUtil.isNotNull(user.getNickName())) {
            update.setNickName(user.getNickName());
        }
        update.setPhonenumber(user.getPhonenumber());
        update.setEmail(user.getEmail());
        update.setSex(user.getSex());
        return baseMapper.updateByQueryWithPermission(update, QueryWrapper.create()
            .eq(SysUser::getUserId, user.getUserId()));
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(Long userId, Long avatar) {
        SysUser update = new SysUser();
        update.setAvatar(avatar);
        return baseMapper.updateByQueryWithPermission(update, QueryWrapper.create()
            .eq(SysUser::getUserId, userId)) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(Long userId, String password) {
        SysUser update = new SysUser();
        update.setPassword(password);
        return baseMapper.updateByQueryWithPermission(update, QueryWrapper.create()
            .eq(SysUser::getUserId, userId));
    }

    /**
     * 新增用户角色信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private void insertUserRole(SysUserBo user, boolean clear) {
        this.insertUserRole(user.getUserId(), user.getRoleIds(), clear);
    }

    /**
     * 新增用户岗位信息
     *
     * @param user  用户对象
     * @param clear 清除已存在的关联数据
     */
    private void insertUserPost(SysUserBo user, boolean clear) {
        Long[] postIdArr = user.getPostIds();
        if (ArrayUtil.isEmpty(postIdArr)) {
            return;
        }
        List<Long> postIds = Arrays.asList(postIdArr);

        // 校验是否有权限操作这些岗位（含数据权限控制）
        if (postMapper.selectPostCount(postIds) != postIds.size()) {
            throw new ServiceException("没有权限访问岗位的数据");
        }

        // 是否清除旧的用户岗位绑定
        if (clear) {
            userPostMapper.deleteByQuery(QueryWrapper.create()
                .eq(SysUserPost::getUserId, user.getUserId()));
        }

        // 构建用户岗位关联列表并批量插入
        List<SysUserPost> list = StreamUtils.toList(postIds,
            postId -> {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                return up;
            });
        userPostMapper.insertBatch(list);
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     * @param clear   清除已存在的关联数据
     */
    private void insertUserRole(Long userId, Long[] roleIds, boolean clear) {
        if (ArrayUtil.isEmpty(roleIds)) {
            return;
        }

        List<Long> roleList = new ArrayList<>(Arrays.asList(roleIds));

        // 非超级管理员，禁止包含超级管理员角色
        if (!LoginHelper.isSuperAdmin(userId)) {
            roleList.remove(SystemConstants.SUPER_ADMIN_ID);
        }

        // 移除超管角色后若无剩余角色，说明仅选了超管角色且不允许分配，显式报错
        if (roleList.isEmpty()) {
            throw new ServiceException("不允许为普通用户分配超级管理员角色，请至少选择一个其他角色");
        }

        // 校验是否有权限访问这些角色（含数据权限控制）
        if (roleMapper.selectRoleCount(roleList) != roleList.size()) {
            throw new ServiceException("没有权限访问角色的数据");
        }

        // 是否清除原有绑定
        if (clear) {
            userRoleMapper.deleteByQuery(QueryWrapper.create()
                .eq(SysUserRole::getUserId, userId));
        }

        // 批量插入用户-角色关联
        List<SysUserRole> list = StreamUtils.toList(roleList,
            roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            });
        userRoleMapper.insertBatch(list);
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteByQuery(QueryWrapper.create()
            .eq(SysUserRole::getUserId, userId));
        // 删除用户与岗位表
        userPostMapper.deleteByQuery(QueryWrapper.create()
            .eq(SysUserPost::getUserId, userId));
        // 防止更新失败导致的数据删除
        int flag = baseMapper.deleteById(userId);
        if (flag < 1) {
            throw new ServiceException("删除用户失败!");
        }
        return flag;
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(userId);
            checkUserDataScope(userId);
        }
        List<Long> ids = List.of(userIds);
        // 删除用户与角色关联
        userRoleMapper.deleteByQuery(QueryWrapper.create()
            .in(SysUserRole::getUserId, ids));
        // 删除用户与岗位表
        userPostMapper.deleteByQuery(QueryWrapper.create()
            .in(SysUserPost::getUserId, ids));
        // 防止更新失败导致的数据删除
        int flag = baseMapper.deleteBatchByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除用户失败!");
        }
        return flag;
    }

    /**
     * 通过部门id查询当前部门所有用户
     *
     * @param deptId 部门ID
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUserVo> selectUserListByDept(Long deptId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(SysUser::getDeptId, deptId)
            .orderBy(SysUser::getUserId, true);
        return baseMapper.selectVoList(queryWrapper);
    }

    @Override
    public List<Long> selectUserIdsByRoleIds(List<Long> roleIds) {
        List<SysUserRole> userRoles = userRoleMapper.selectListByQuery(QueryWrapper.create()
            .in(SysUserRole::getRoleId, roleIds));
        return StreamUtils.toList(userRoles, SysUserRole::getUserId);
    }

    /**
     * 通过用户ID查询用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    @Cacheable(cacheNames = CacheNames.SYS_USER_NAME, key = "#userId")
    @Override
    public String selectUserNameById(Long userId) {
        SysUser sysUser = baseMapper.selectOneByQuery(QueryWrapper.create()
            .select(SYS_USER.USER_NAME)
            .where(SYS_USER.USER_ID.eq(userId)));
        return ObjectUtils.notNullGetter(sysUser, SysUser::getUserName);
    }

    /**
     * 通过用户ID查询用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称
     */
    @Override
    @Cacheable(cacheNames = CacheNames.SYS_NICKNAME, key = "#userId")
    public String selectNicknameById(Long userId) {
        SysUser sysUser = baseMapper.selectOneByQuery(QueryWrapper.create()
            .select(SYS_USER.NICK_NAME)
            .where(SYS_USER.USER_ID.eq(userId)));
        return ObjectUtils.notNullGetter(sysUser, SysUser::getNickName);
    }

    @Override
    public String selectNicknameByIds(String userIds) {
        List<String> list = new ArrayList<>();
        for (Long id : StringUtils.splitTo(userIds, ConvertUtil::toLong)) {
            String nickname = SpringUtils.getAopProxy(this).selectNicknameById(id);
            if (StringUtils.isNotBlank(nickname)) {
                list.add(nickname);
            }
        }
        return StringUtils.joinComma(list);
    }

    /**
     * 通过用户ID查询用户手机号
     *
     * @param userId 用户id
     * @return 用户手机号
     */
    @Override
    public String selectPhonenumberById(Long userId) {
        SysUser sysUser = baseMapper.selectOneByQuery(QueryWrapper.create()
            .select(SYS_USER.PHONENUMBER)
            .where(SYS_USER.USER_ID.eq(userId)));
        return ObjectUtils.notNullGetter(sysUser, SysUser::getPhonenumber);
    }

    /**
     * 通过用户ID查询用户邮箱
     *
     * @param userId 用户id
     * @return 用户邮箱
     */
    @Override
    public String selectEmailById(Long userId) {
        SysUser sysUser = baseMapper.selectOneByQuery(QueryWrapper.create()
            .select(SYS_USER.EMAIL)
            .where(SYS_USER.USER_ID.eq(userId)));
        return ObjectUtils.notNullGetter(sysUser, SysUser::getEmail);
    }

}
