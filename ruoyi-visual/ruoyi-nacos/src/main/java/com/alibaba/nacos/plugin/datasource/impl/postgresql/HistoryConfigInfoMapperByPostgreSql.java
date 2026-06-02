package com.alibaba.nacos.plugin.datasource.impl.postgresql;

import com.alibaba.nacos.plugin.datasource.mapper.HistoryConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.List;

/**
 * PostgreSQL does not support MySQL's DELETE ... LIMIT syntax.
 */
public class HistoryConfigInfoMapperByPostgreSql extends AbstractMapperByPostgreSql
    implements HistoryConfigInfoMapper {

    @Override
    public MapperResult removeConfigHistory(MapperContext context) {
        String sql = "DELETE FROM his_config_info WHERE ctid IN "
            + "(SELECT ctid FROM his_config_info WHERE gmt_modified < ? LIMIT ?)";
        return new MapperResult(sql, List.of(
            context.getWhereParameter("startTime"),
            context.getWhereParameter("limitSize")
        ));
    }

    @Override
    public MapperResult pageFindConfigHistoryFetchRows(MapperContext context) {
        String sql = "SELECT nid,data_id,group_id,tenant_id,app_name,src_ip,src_user,op_type,"
            + "ext_info,publish_type,gray_name,gmt_create,gmt_modified FROM his_config_info "
            + "WHERE data_id = ? AND group_id = ? AND tenant_id = ? ORDER BY nid DESC OFFSET ? LIMIT ?";
        return new MapperResult(sql, List.of(
            context.getWhereParameter("dataId"),
            context.getWhereParameter("groupId"),
            context.getWhereParameter("tenantId"),
            context.getStartRow(),
            context.getPageSize()
        ));
    }

    @Override
    public String getDataSource() {
        return "postgresql";
    }
}
