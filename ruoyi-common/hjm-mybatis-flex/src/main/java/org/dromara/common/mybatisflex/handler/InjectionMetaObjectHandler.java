package org.dromara.common.mybatisflex.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.ObjectUtils;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.model.LoginUser;

import java.util.Date;

/**
 * MyBatis-Flex注入处理器
 *
 * @author Lion Li
 */
@Slf4j
public class InjectionMetaObjectHandler implements InsertListener, UpdateListener {

    /**
     * 如果用户不存在默认注入-1代表无用户
     */
    private static final Long DEFAULT_USER_ID = -1L;

    /**
     * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
     *
     * @param entity 待填充实体
     */
    @Override
    public void onInsert(Object entity) {
        try {
            if (!(entity instanceof BaseEntity baseEntity)) {
                return;
            }
            // 获取当前时间作为创建时间和更新时间，如果创建时间不为空，则使用创建时间，否则使用当前时间
            Date current = ObjectUtils.notNull(baseEntity.getCreateTime(), new Date());
            baseEntity.setCreateTime(current);
            baseEntity.setUpdateTime(current);

            // 如果创建人为空，则填充当前登录用户的信息
            if (ObjectUtil.isNull(baseEntity.getCreateBy())) {
                LoginUser loginUser = getLoginUser();
                if (ObjectUtil.isNotNull(loginUser)) {
                    Long userId = loginUser.getUserId();
                    // 填充创建人、更新人和创建部门信息
                    baseEntity.setCreateBy(userId);
                    baseEntity.setUpdateBy(userId);
                    baseEntity.setCreateDept(ObjectUtils.notNull(baseEntity.getCreateDept(), loginUser.getDeptId()));
                } else {
                    // 填充创建人、更新人和创建部门信息
                    baseEntity.setCreateBy(DEFAULT_USER_ID);
                    baseEntity.setUpdateBy(DEFAULT_USER_ID);
                    baseEntity.setCreateDept(ObjectUtils.notNull(baseEntity.getCreateDept(), DEFAULT_USER_ID));
                }
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 更新填充方法，用于在更新数据时自动填充实体对象中的更新时间和更新人信息
     *
     * @param entity 待填充实体
     */
    @Override
    public void onUpdate(Object entity) {
        try {
            if (!(entity instanceof BaseEntity baseEntity)) {
                return;
            }
            // 获取当前时间作为更新时间，无论原始对象中的更新时间是否为空都填充
            Date current = new Date();
            baseEntity.setUpdateTime(current);

            // 获取当前登录用户的ID，并填充更新人信息
            Long userId = LoginHelper.getUserId();
            if (ObjectUtil.isNotNull(userId)) {
                baseEntity.setUpdateBy(userId);
            } else {
                baseEntity.setUpdateBy(DEFAULT_USER_ID);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户的信息，如果用户未登录则返回 null
     */
    private LoginUser getLoginUser() {
        LoginUser loginUser;
        try {
            loginUser = LoginHelper.getLoginUser();
        } catch (Exception e) {
            return null;
        }
        return loginUser;
    }

}
