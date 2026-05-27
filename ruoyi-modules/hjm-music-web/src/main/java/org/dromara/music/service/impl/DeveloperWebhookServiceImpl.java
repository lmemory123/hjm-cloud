package org.dromara.music.service.impl;

import cn.hutool.v7.core.map.Dict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.config.VirtualThreadExecutionConfig;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatisflex.helper.DataBaseHelper;
import org.dromara.music.constant.DeveloperApiConstants;
import org.dromara.music.domain.MusicWebhookLog;
import org.dromara.music.domain.bo.DeveloperWebhookDispatchBo;
import org.dromara.music.mapper.MusicWebhookLogMapper;
import org.dromara.music.service.IDeveloperWebhookService;
import org.dromara.system.api.RemoteConfigService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * 开发者 Webhook 服务实现
 *
 * @author momao
 * @date 2026-05-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeveloperWebhookServiceImpl implements IDeveloperWebhookService {

    private final MusicWebhookLogMapper webhookLogMapper;
    private final HttpClient webhookClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    @DubboReference
    private RemoteConfigService remoteConfigService;

    @Async(VirtualThreadExecutionConfig.ASYNC_TASK_EXECUTOR_BEAN)
    @Override
    public void pushAsync(String clientId, DeveloperWebhookDispatchBo bo) {
        List<Dict> targets = safeArrayConfig(DeveloperApiConstants.WEBHOOKS_CONFIG_KEY);
        for (Dict target : targets) {
            if (!isEnabled(target.get("enabled")) || !supportsEvent(target.get("eventTypes"), bo.getEventType())) {
                continue;
            }
            String url = stringValue(target.get("url"));
            if (StringUtils.isBlank(url)) {
                continue;
            }
            sendWithRetry(target, url, clientId, bo);
        }
    }

    private void sendWithRetry(Dict target, String url, String clientId, DeveloperWebhookDispatchBo bo) {
        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;
        String lastMessage = null;
        Integer statusCode = null;

        while (attempt <= maxRetries && !success) {
            try {
                String timestamp = String.valueOf(System.currentTimeMillis());
                Map<String, Object> payload = createPayload(clientId, bo, timestamp);
                String jsonPayload = JsonUtils.toJsonString(payload);

                HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header(DeveloperApiConstants.WEBHOOK_EVENT_HEADER, bo.getEventType())
                    .header(DeveloperApiConstants.WEBHOOK_TIMESTAMP_HEADER, timestamp)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload));

                String secret = stringValue(target.get("secret"));
                if (StringUtils.isNotBlank(secret)) {
                    builder.header(DeveloperApiConstants.WEBHOOK_SECRET_HEADER, secret);
                    builder.header(DeveloperApiConstants.WEBHOOK_SIGNATURE_HEADER, generateSignature(jsonPayload, secret, timestamp));
                }

                HttpResponse<String> response = webhookClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                statusCode = response.statusCode();
                success = statusCode >= 200 && statusCode < 300;
                lastMessage = success ? "OK" : StringUtils.substring(response.body(), 0, 1000);
            } catch (Exception e) {
                lastMessage = StringUtils.substring(e.getMessage(), 0, 1000);
                log.warn("Webhook 推送异常, url={}, attempt={}", url, attempt, e);
            }

            if (!success && attempt < maxRetries) {
                attempt++;
                try {
                    // 指数退避
                    Thread.sleep((long) Math.pow(2, attempt - 1) * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else {
                break;
            }
        }

        // 记录日志
        saveLog(target, url, clientId, bo, statusCode, success, lastMessage, attempt);
    }

    private String generateSignature(String payload, String secret, String timestamp) {
        try {
            String base = timestamp + "." + payload;
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] hash = sha256HMAC.doFinal(base.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("计算 Webhook 签名失败", e);
            return "";
        }
    }

    private void saveLog(Dict target, String url, String clientId, DeveloperWebhookDispatchBo bo,
                         Integer statusCode, boolean success, String message, int retryCount) {
        try {
            MusicWebhookLog webhookLog = new MusicWebhookLog();
            webhookLog.setId(DataBaseHelper.nextId());
            webhookLog.setWebhookId(stringValue(target.get("id")));
            webhookLog.setWebhookName(stringValue(target.get("name")));
            webhookLog.setUrl(url);
            webhookLog.setEventType(bo.getEventType());
            webhookLog.setPayload(JsonUtils.toJsonString(createPayload(clientId, bo, String.valueOf(System.currentTimeMillis()))));
            webhookLog.setStatusCode(statusCode);
            webhookLog.setSuccess(success ? "1" : "0");
            webhookLog.setMessage(message);
            webhookLog.setRetryCount(retryCount);
            webhookLog.setClientId(clientId);
            webhookLog.setCreateTime(new Date());
            webhookLogMapper.insert(webhookLog);
        } catch (Exception e) {
            log.error("保存 Webhook 日志失败", e);
        }
    }

    private Map<String, Object> createPayload(String clientId, DeveloperWebhookDispatchBo bo, String timestamp) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventType", bo.getEventType());
        payload.put("title", bo.getTitle());
        payload.put("content", bo.getContent());
        payload.put("targetUrl", bo.getTargetUrl());
        payload.put("data", bo.getData());
        payload.put("clientId", clientId);
        payload.put("timestamp", timestamp);
        return payload;
    }

    private List<Dict> safeArrayConfig(String configKey) {
        try {
            List<Dict> list = remoteConfigService.getConfigArrayMap(configKey);
            return list == null ? List.of() : list;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private boolean supportsEvent(Object configuredEvents, String eventType) {
        List<String> eventTypes = toStringList(configuredEvents);
        return eventTypes.isEmpty() || eventTypes.contains("*") || eventTypes.contains(eventType);
    }

    private boolean isEnabled(Object value) {
        if (value == null) {
            return true;
        }
        String text = stringValue(value);
        return StringUtils.isBlank(text)
            || StringUtils.equalsAnyIgnoreCase(text, "enabled", "enable", "true", "1", "yes");
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private List<String> toStringList(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                .map(this::stringValue)
                .filter(StringUtils::isNotBlank)
                .toList();
        }
        String text = stringValue(value);
        if (StringUtils.isBlank(text)) {
            return List.of();
        }
        return List.of(text.split(",")).stream()
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .toList();
    }
}
