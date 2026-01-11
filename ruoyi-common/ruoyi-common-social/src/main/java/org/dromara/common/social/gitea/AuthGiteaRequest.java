package org.dromara.common.social.gitea;

import cn.hutool.v7.core.map.Dict;
import cn.hutool.v7.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.json.utils.JsonUtils;

import java.util.Map;

/**
 * @author lcry
 */
@Slf4j
public class AuthGiteaRequest extends AuthDefaultRequest {

    public static final String SERVER_URL = SpringUtils.getProperty("justauth.type.gitea.server-url");

    /**
     * 设定归属域
     */
    public AuthGiteaRequest(AuthConfig config) {
        super(config, AuthGiteaSource.GITEA);
    }

    public AuthGiteaRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthGiteaSource.GITEA, authStateCache);
    }

    @Override
    public AuthToken getAccessToken(AuthCallback authCallback) {
        String body = doPostAuthorizationCode(authCallback.getCode());
        Dict object = JsonUtils.parseMap(body);
        // oauth/token 验证异常
        if (object.containsKey("error")) {
            throw new AuthException(object.getStr("error_description"));
        }
        // user 验证异常
        if (object.containsKey("message")) {
            throw new AuthException(object.getStr("message"));
        }
        return AuthToken.builder()
                .accessToken(object.getStr("access_token"))
                .refreshToken(object.getStr("refresh_token"))
                .idToken(object.getStr("id_token"))
                .tokenType(object.getStr("token_type"))
                .scope(object.getStr("scope"))
                .build();
    }

    @Override
    protected String doPostAuthorizationCode(String code) {
        Map<String,Object> parms = Map.of(
                "client_id", config.getClientId(),
                "client_secret", config.getClientSecret(),
                "grant_type", "authorization_code",
                "code", code,
                "redirect_uri", config.getRedirectUri()
        );
       return HttpUtil.post(source.accessToken(),parms);

    }

    @Override
    public AuthUser getUserInfo(AuthToken authToken) {
        String body = doGetUserInfo(authToken);
        Dict object = JsonUtils.parseMap(body);
        // oauth/token 验证异常
        if (object.containsKey("error")) {
            throw new AuthException(object.getStr("error_description"));
        }
        // user 验证异常
        if (object.containsKey("message")) {
            throw new AuthException(object.getStr("message"));
        }
        return AuthUser.builder()
                .uuid(object.getStr("sub"))
                .username(object.getStr("name"))
                .nickname(object.getStr("preferred_username"))
                .avatar(object.getStr("picture"))
                .email(object.getStr("email"))
                .token(authToken)
                .source(source.toString())
                .build();
    }

}
