package org.dromara.common.json.utils;

import cn.hutool.v7.core.array.ArrayUtil;
import cn.hutool.v7.core.map.Dict;
import cn.hutool.v7.core.util.ObjUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON 工具类
 *
 * @author 芋道源码
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static JsonMapper getObjectMapper() {
        return SpringUtils.getBean(JsonMapper.class);
    }

    public static String toJsonString(Object object) {
        if (ObjUtil.isNull(object)) {
            return null;
        }
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        return getObjectMapper().readValue(text, clazz);
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }
        return getObjectMapper().readValue(bytes, clazz);
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return getObjectMapper().readValue(text, typeReference);
    }

    public static Dict parseMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return getObjectMapper().readValue(text, getObjectMapper().getTypeFactory().constructType(Dict.class));
        } catch (MismatchedInputException e) {
            return null;
        }
    }

    public static List<Dict> parseArrayMap(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return getObjectMapper().readValue(text, getObjectMapper().getTypeFactory().constructCollectionType(List.class, Dict.class));
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        return getObjectMapper().readValue(text, getObjectMapper().getTypeFactory().constructCollectionType(List.class, clazz));
    }

    public static boolean isJson(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            getObjectMapper().readTree(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJsonObject(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            JsonNode node = getObjectMapper().readTree(str);
            return node.isObject();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJsonArray(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            JsonNode node = getObjectMapper().readTree(str);
            return node.isArray();
        } catch (Exception e) {
            return false;
        }
    }


}

