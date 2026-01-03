package org.dromara.common.mybatisflex.dialect;

import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.SqlserverDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import org.dromara.common.mybatisflex.handler.PlusDataPermissionHandler;

/**
 * SQL Server data permission dialect.
 *
 * @author Lion Li
 */
public class AuthSqlServerDialect extends SqlserverDialectImpl {

    private final PlusDataPermissionHandler dataPermissionHandler = new PlusDataPermissionHandler();

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        DataPermissionDialectSupport.apply(dataPermissionHandler, queryWrapper, operateType);
        super.prepareAuth(queryWrapper, operateType);
    }

    @Override
    public void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
        DataPermissionDialectSupport.apply(dataPermissionHandler, sql, operateType);
        super.prepareAuth(schema, tableName, sql, operateType);
    }

    @Override
    public void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
        DataPermissionDialectSupport.apply(dataPermissionHandler, sql, operateType);
        super.prepareAuth(tableInfo, sql, operateType);
    }
}
