package org.dromara.common.web.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * 获取请求头国际化信息
 *
 * @author Lion Li
 */
public class I18nLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String language = httpServletRequest.getHeader("content-language");
        Locale locale = Locale.getDefault();
        if (language != null && !language.isEmpty()) {
            String[] split = language.split("_");
            locale = new Locale.Builder().setLanguage(split[0]).setRegion(split[1]).build();
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
