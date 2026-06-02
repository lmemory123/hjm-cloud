package org.dromara.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.util.ObjUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.form.RegisterBody;
import org.dromara.auth.properties.CaptchaProperties;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.exception.user.CaptchaException;
import org.dromara.common.core.exception.user.CaptchaExpireException;
import org.dromara.common.core.exception.user.UserException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.log.event.LogininforEvent;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteSocialService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.bo.RemoteSocialBo;
import org.dromara.system.api.domain.bo.RemoteUserBo;
import org.dromara.system.api.domain.vo.RemoteSocialVo;
import org.dromara.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 登录校验方法
 *
 * @author Lion Li
 */
@Slf4j
@Service
public class SysLoginService {

    @DubboReference
    private RemoteUserService remoteUserService;
    @DubboReference
    private RemoteSocialService remoteSocialService;

    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 登录
     */
    public void login(String username, String password) {
        LoginUser user = remoteUserService.getUserInfo(username);
        checkLogin(LoginType.PASSWORD, username, () -> !BCrypt.checkpw(password, user.getPassword()));
    }

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        // 校验用户类型是否存在
        String userType = UserType.getUserType(registerBody.getUserType()).getUserType();

        boolean captchaEnabled = captchaProperties.getEnabled();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        // 注册用户信息
        RemoteUserBo remoteUserBo = new RemoteUserBo();
        remoteUserBo.setUserName(username);
        remoteUserBo.setNickName(username);
        remoteUserBo.setPassword(BCrypt.hashpw(password));
        remoteUserBo.setUserType(userType);

        boolean regFlag = remoteUserService.registerUserInfo(remoteUserBo);
        if (!regFlag) {
            throw new UserException("user.register.error");
        }
        recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    /**
     * 退出登录
     */
    public void logout() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser != null) {
            recordLogininfor(loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
        }
        StpUtil.logout();
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表
     */
    public void recordLogininfor(String username, String status, String message, Object... args) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setArgs(args);
        SpringUtils.context().publishEvent(logininforEvent);
    }

    /**
     * 校验短信验证码
     */
    public boolean validateSmsCode(String phonenumber, String smsCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber);
        if (StringUtils.isBlank(code)) {
            recordLogininfor(phonenumber, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(smsCode);
    }

    /**
     * 校验邮箱验证码
     */
    public boolean validateEmailCode(String email, String emailCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + email);
        if (StringUtils.isBlank(code)) {
            recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(emailCode);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 登录校验
     */
    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        String errorKey = CacheConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;

        // 获取用户登录错误次数，累计错误次数10分钟有效
        Integer errorNumber = RedisUtils.getCacheObject(errorKey);

        // 锁定时间，默认10分钟
        int lockTime = 10;

        // 判断是否有锁
        if (ObjUtil.isNotNull(errorNumber) && errorNumber >= Constants.PASSWORD_MAX_RETRY_COUNT) {
            recordLogininfor(username, loginFail, MessageUtils.message("user.password.retry.limit.exceed", Constants.PASSWORD_MAX_RETRY_COUNT, lockTime));
            throw new UserException("user.password.retry.limit.exceed", Constants.PASSWORD_MAX_RETRY_COUNT, lockTime);
        }

        if (supplier.get()) {
            // 是否第一次错误
            errorNumber = ObjUtil.isNull(errorNumber) ? 1 : errorNumber + 1;
            // 达到最大错误次数记录次数
            if (errorNumber >= Constants.PASSWORD_MAX_RETRY_COUNT) {
                RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
                recordLogininfor(username, loginFail, MessageUtils.message("user.password.retry.limit.exceed", Constants.PASSWORD_MAX_RETRY_COUNT, lockTime));
                throw new UserException("user.password.retry.limit.exceed", Constants.PASSWORD_MAX_RETRY_COUNT, lockTime);
            } else {
                // 未达到最大错误次数记录次数
                RedisUtils.setCacheObject(errorKey, errorNumber);
                recordLogininfor(username, loginFail, MessageUtils.message("user.password.retry.limit.count", errorNumber));
                throw new UserException("user.password.retry.limit.count", errorNumber);
            }
        }

        // 登录成功清除错误次数
        RedisUtils.deleteObject(errorKey);
    }

    /**
     * 第三方登录业务处理
     */
    public void socialRegister(AuthUser authUserData) {
        RemoteSocialBo socialBo = new RemoteSocialBo();
        socialBo.setUserId(LoginHelper.getUserId());
        socialBo.setAuthId(authUserData.getSource() + authUserData.getUuid());
        socialBo.setSource(authUserData.getSource());
        socialBo.setAccessToken(authUserData.getToken().getAccessToken());
        socialBo.setRefreshToken(authUserData.getToken().getRefreshToken());
        socialBo.setExpireIn(authUserData.getToken().getExpireIn());
        socialBo.setUserName(authUserData.getUsername());
        socialBo.setNickName(authUserData.getNickname());
        socialBo.setAvatar(authUserData.getAvatar());
        socialBo.setOpenId(authUserData.getUuid());
        remoteSocialService.updateByBo(socialBo);
    }

}
