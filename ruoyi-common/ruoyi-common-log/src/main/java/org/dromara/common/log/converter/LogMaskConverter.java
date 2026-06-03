package org.dromara.common.log.converter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.dromara.common.core.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志脱敏转换器
 *
 * @author momao
 */
public class LogMaskConverter extends MessageConverter {

    /**
     * 手机号正则
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    /**
     * Token正则 (Bearer xxx)
     */
    private static final Pattern TOKEN_PATTERN = Pattern.compile("Bearer\\s+[a-zA-Z0-9._-]+");

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (StringUtils.isBlank(message)) {
            return message;
        }
        
        // 脱敏手机号
        message = mask(message, PHONE_PATTERN, 3, 4);
        
        // 脱敏邮箱
        message = maskEmail(message);
        
        // 脱敏Token
        message = maskToken(message);
        
        return message;
    }

    private String mask(String content, Pattern pattern, int prefixLen, int suffixLen) {
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group();
            if (match.length() > prefixLen + suffixLen) {
                String masked = match.substring(0, prefixLen) + "****" + match.substring(match.length() - suffixLen);
                matcher.appendReplacement(sb, masked);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String maskEmail(String content) {
        Matcher matcher = EMAIL_PATTERN.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group();
            int atIndex = match.indexOf("@");
            if (atIndex > 1) {
                String masked = match.substring(0, 1) + "****" + match.substring(atIndex);
                matcher.appendReplacement(sb, masked);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String maskToken(String content) {
        Matcher matcher = TOKEN_PATTERN.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "Bearer ******");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
