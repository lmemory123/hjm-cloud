package org.dromara.gateway.filter;

import cn.hutool.core.map.MapUtil;
import cn.hutool.v7.core.array.ArrayUtil;
import cn.hutool.v7.core.util.ObjUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.gateway.config.properties.ApiDecryptProperties;
import org.dromara.gateway.config.properties.CustomGatewayProperties;
import org.dromara.gateway.utils.WebFluxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 全局日志过滤器
 * <p>
 * 用于打印请求执行参数与响应时间等等
 *
 * @author Lion Li
 */
@Slf4j
@Component
public class GlobalLogFilter implements GlobalFilter, Ordered {

    @Autowired
    private CustomGatewayProperties customGatewayProperties;
    @Autowired
    private ApiDecryptProperties apiDecryptProperties;

    private static final String START_TIME = "startTime";

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!customGatewayProperties.getRequestLog()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String path = WebFluxUtils.getOriginalRequestUrl(exchange);
        String url = request.getMethod().name() + " " + path;

        // 打印请求参数
        if (WebFluxUtils.isJsonRequest(exchange)) {
            if (apiDecryptProperties.getEnabled()
                && ObjUtil.isNotNull(request.getHeaders().getFirst(apiDecryptProperties.getHeaderFlag()))) {
                log.info("[PLUS]开始请求 => URL[{}],参数类型[encrypt]", url);
            } else {
                String jsonParam = WebFluxUtils.resolveBodyFromCacheRequest(exchange);
                if (StringUtils.isNotBlank(jsonParam)) {
                    ObjectMapper objectMapper = JsonUtils.getObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(jsonParam);
                    removeSensitiveFields(rootNode, SystemConstants.EXCLUDE_PROPERTIES);
                    jsonParam = rootNode.toString();
                }
                log.info("[PLUS]开始请求 => URL[{}],参数类型[json],参数:[{}]", url, jsonParam);
            }
        } else {
            MultiValueMap<String, String> parameterMap = request.getQueryParams();
            if (MapUtil.isNotEmpty(parameterMap)) {
                LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>(parameterMap);
                MapUtil.removeAny(map, SystemConstants.EXCLUDE_PROPERTIES);
                String parameters = JsonUtils.toJsonString(map);
                log.info("[PLUS]开始请求 => URL[{}],参数类型[param],参数:[{}]", url, parameters);
            } else {
                log.info("[PLUS]开始请求 => URL[{}],无参数", url);
            }
        }

        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                log.info("[PLUS]结束请求 => URL[{}],耗时:[{}]毫秒", url, executeTime);
            }
        }));
    }

    private void removeSensitiveFields(JsonNode node, String[] excludeProperties) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Set<String> fieldsToRemove = new HashSet<>();
            objectNode.propertyNames().forEach(propertyName ->{
                if (ArrayUtil.contains(excludeProperties, propertyName)) {
                    fieldsToRemove.add(propertyName);
                }
            });
            fieldsToRemove.forEach(objectNode::remove);
           objectNode.valueStream().forEach(value ->{
               removeSensitiveFields(value, excludeProperties);
           });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (JsonNode child : arrayNode) {
                removeSensitiveFields(child, excludeProperties);
            }
        }
    }

    @Override
    public int getOrder() {
        // 日志处理器在负载均衡器之后执行 负载均衡器会导致线程切换 无法获取上下文内容
        // 如需在日志内操作线程上下文 例如获取登录用户数据等 可以打开下方注释代码
        // return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER - 1;
        return Ordered.LOWEST_PRECEDENCE;
    }

}
