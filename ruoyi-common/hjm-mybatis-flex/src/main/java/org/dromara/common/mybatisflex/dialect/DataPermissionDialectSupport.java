package org.dromara.common.mybatisflex.dialect;

import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.handler.PlusDataPermissionHandler;
import org.dromara.common.mybatisflex.helper.DataPermissionHelper;

import static com.mybatisflex.core.constant.SqlConsts.*;

/**
 * Data permission dialect support.
 *
 * @author Lion Li
 */
public final class DataPermissionDialectSupport {

    private DataPermissionDialectSupport() {
    }

    public static void apply(PlusDataPermissionHandler handler, QueryWrapper queryWrapper, OperateType operateType) {
        if (queryWrapper == null || shouldSkip(handler)) {
            return;
        }
        String sqlSegment = handler.getSqlSegment(operateType == OperateType.SELECT);
        if (StringUtils.isBlank(sqlSegment)) {
            return;
        }
        queryWrapper.and(BRACKET_LEFT + sqlSegment + BRACKET_RIGHT);
    }

    public static void apply(PlusDataPermissionHandler handler, StringBuilder sql, OperateType operateType) {
        if (sql == null || shouldSkip(handler)) {
            return;
        }
        String sqlSegment = handler.getSqlSegment(operateType == OperateType.SELECT);
        if (StringUtils.isBlank(sqlSegment)) {
            return;
        }
        sql.append(AND).append(BRACKET_LEFT).append(sqlSegment).append(BRACKET_RIGHT);
    }

    private static boolean shouldSkip(PlusDataPermissionHandler handler) {
        return DataPermissionHelper.isIgnore() || handler.invalid();
    }
}
