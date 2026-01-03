package org.dromara.system.domain.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 系统模块租户基类
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemTenantEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    private String tenantId;

}
