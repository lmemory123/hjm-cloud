package org.dromara.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.system.domain.base.SystemTenantEntity;

/**
 * 参数配置表 sys_config
 *
 * @author Lion Li
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_config")
public class SysConfig extends SystemTenantEntity {

    /**
     * 参数主键
     */
    @Id
    private Long configId;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;

    /**
     * 备注
     */
    private String remark;

}
