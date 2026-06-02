package org.dromara.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.v7.core.util.ObjUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.FrontPasswordLoginBody;
import org.dromara.auth.form.FrontRegisterBody;
import org.dromara.auth.properties.CaptchaProperties;
import org.dromara.auth.properties.FrontAuthProperties;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.exception.user.UserException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.resource.api.RemoteMessageService;
import org.dromara.system.api.RemoteClientService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.bo.RemoteUserBo;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;

/**
 * 前台认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FrontAuthService {

    private final CaptchaProperties captchaProperties;
    private final FrontAuthProperties frontAuthProperties;
    private final SysLoginService sysLoginService;

    @DubboReference
    private final RemoteUserService remoteUserService;
    @DubboReference
    private final RemoteClientService remoteClientService;

    public LoginVo login(FrontPasswordLoginBody body) {
        ValidatorUtils.validate(body);
        RemoteClientVo clientVo = getFrontClient();
        String tenantId = TenantConstants.DEFAULT_TENANT_ID;
        String username = body.getUsername();

        if (Boolean.TRUE.equals(captchaProperties.getEnabled())) {
            sysLoginService.validateCaptcha(tenantId, username, body.getCode(), body.getUuid());
        }

        LoginUser loginUser = remoteUserService.getUserInfo(username);
        if (ObjUtil.isNull(loginUser) || !Strings.CS.equals(loginUser.getUserType(), UserType.APP_USER.getUserType())) {
            sysLoginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, "前台用户不存在或类型不匹配");
            throw new UserException("user.not.exists", username);
        }
        sysLoginService.checkLogin(LoginType.PASSWORD, tenantId, username,
            () -> !BCrypt.checkpw(body.getPassword(), loginUser.getPassword()));

        loginUser.setClientKey(clientVo.getClientKey());
        loginUser.setDeviceType(clientVo.getDeviceType());

        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(clientVo.getDeviceType());
        model.setTimeout(clientVo.getTimeout());
        model.setActiveTimeout(clientVo.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, clientVo.getClientId());
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(clientVo.getClientId());
        return loginVo;
    }

    public void register(FrontRegisterBody body) {
        ValidatorUtils.validate(body);
        String tenantId = TenantConstants.DEFAULT_TENANT_ID;
        String username = body.getUsername();
        String password = body.getPassword();

        if (Boolean.TRUE.equals(captchaProperties.getEnabled())) {
            sysLoginService.validateCaptcha(tenantId, username, body.getCode(), body.getUuid());
        }
        if (!Boolean.TRUE.equals(frontAuthProperties.getRegisterEnabled())) {
            throw new ServiceException("当前系统没有开启前台注册功能！");
        }

        RemoteUserBo remoteUserBo = new RemoteUserBo();
        remoteUserBo.setUserName(username);
        remoteUserBo.setNickName(username);
        remoteUserBo.setPassword(BCrypt.hashpw(password));
        remoteUserBo.setUserType(UserType.APP_USER.getUserType());

        boolean regFlag = remoteUserService.registerFrontUserInfo(remoteUserBo);
        if (!regFlag) {
            throw new UserException("user.register.error");
        }
        sysLoginService.recordLogininfor(tenantId, username, Constants.REGISTER, MessageUtils.message("user.register.success"));
    }

    private RemoteClientVo getFrontClient() {
        if (StringUtils.isBlank(frontAuthProperties.getClientId())) {
            throw new ServiceException("未配置前台登录 clientId，请先设置 security.front-auth.client-id");
        }
        RemoteClientVo clientVo = remoteClientService.queryByClientId(frontAuthProperties.getClientId());
        if (ObjUtil.isNull(clientVo) || !Strings.CS.contains(clientVo.getGrantType(), frontAuthProperties.getGrantType())) {
            log.info("前台客户端id: {} 认证类型: {} 异常", frontAuthProperties.getClientId(), frontAuthProperties.getGrantType());
            throw new ServiceException(MessageUtils.message("auth.grant.type.error"));
        }
        if (!SystemConstants.NORMAL.equals(clientVo.getStatus())) {
            throw new ServiceException(MessageUtils.message("auth.grant.type.blocked"));
        }
        return clientVo;
    }

}
