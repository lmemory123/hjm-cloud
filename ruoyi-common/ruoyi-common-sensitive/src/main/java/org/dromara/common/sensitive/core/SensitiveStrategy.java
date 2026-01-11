package org.dromara.common.sensitive.core;

import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.data.masking.MaskingUtil;
import lombok.AllArgsConstructor;
import org.dromara.common.core.utils.DesensitizedUtils;

import java.util.function.Function;

/**
 * 脱敏策略
 *
 * @author Yjoioooo
 * @version 3.6.0
 */
@AllArgsConstructor
public enum SensitiveStrategy {

    /**
     * 身份证脱敏
     */
    ID_CARD(s -> MaskingUtil.idCardNum(s, 3, 4)),

    /**
     * 手机号脱敏
     */
    PHONE(MaskingUtil::mobilePhone),

    /**
     * 地址脱敏
     */
    ADDRESS(s -> MaskingUtil.address(s, 8)),

    /**
     * 邮箱脱敏
     */
    EMAIL(MaskingUtil::email),

    /**
     * 银行卡
     */
    BANK_CARD(MaskingUtil::bankCard),

    /**
     * 中文名
     */
    CHINESE_NAME(MaskingUtil::chineseName),

    /**
     * 固定电话
     */
    FIXED_PHONE(MaskingUtil::fixedPhone),

    /**
     * 用户ID
     */
    USER_ID(s -> ConvertUtil.toStr(MaskingUtil.userId())),

    /**
     * 密码
     */
    PASSWORD(MaskingUtil::password),

    /**
     * ipv4
     */
    IPV4(MaskingUtil::ipv4),

    /**
     * ipv6
     */
    IPV6(MaskingUtil::ipv6),

    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE(MaskingUtil::carLicense),

    /**
     * 只显示第一个字符
     */
    FIRST_MASK(MaskingUtil::firstMask),

    /**
     * 通用字符串脱敏
     * 可配置前后可见长度和中间掩码长度
     * 默认示例：前4位可见，后4位可见，中间固定4个*
     */
    STRING_MASK(s -> DesensitizedUtils.mask(s, 4, 4, 4)),

    /**
     * 清空为null
     */
    CLEAR(s -> MaskingUtil.clear()),

    /**
     * 清空为""
     */
    CLEAR_TO_NULL(s -> MaskingUtil.clearToNull());

    //可自行添加其他脱敏策略

    private final Function<String, String> desensitizer;

    public Function<String, String> desensitizer() {
        return desensitizer;
    }
}
