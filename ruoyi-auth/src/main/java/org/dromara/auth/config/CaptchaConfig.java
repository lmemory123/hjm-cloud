package org.dromara.auth.config;

import cn.hutool.v7.swing.captcha.CaptchaUtil;
import cn.hutool.v7.swing.captcha.CircleCaptcha;
import cn.hutool.v7.swing.captcha.LineCaptcha;
import cn.hutool.v7.swing.captcha.ShearCaptcha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.awt.*;

/**
 * 验证码配置
 *
 * @author Lion Li
 */
@Configuration
public class CaptchaConfig {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 60;
    private static final Color BACKGROUND = Color.LIGHT_GRAY;
    private static final Font FONT = new Font("Arial", Font.BOLD, 48);

    /**
     * 圆圈干扰验证码
     */
    @Lazy
    @Bean
    public CircleCaptcha circleCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.ofCircleCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 线段干扰的验证码
     */
    @Lazy
    @Bean
    public LineCaptcha lineCaptcha() {
        LineCaptcha captcha = CaptchaUtil.ofLineCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 扭曲干扰验证码
     */
    @Lazy
    @Bean
    public ShearCaptcha shearCaptcha() {
        ShearCaptcha captcha = CaptchaUtil.ofShearCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

}
