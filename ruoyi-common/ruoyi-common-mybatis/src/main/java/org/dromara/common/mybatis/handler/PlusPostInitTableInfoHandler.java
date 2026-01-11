package org.dromara.common.mybatis.handler;

import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.reflect.FieldUtil;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;
import org.dromara.common.core.utils.SpringUtils;

/**
 * 修改表信息初始化方式
 * 目前用于全局修改是否使用逻辑删除
 *
 * @author Lion Li
 */
public class PlusPostInitTableInfoHandler implements PostInitTableInfoHandler {

    @Override
    public void postTableInfo(TableInfo tableInfo, Configuration configuration) {
        String flag = SpringUtils.getProperty("mybatis-plus.enableLogicDelete", "true");
        // 只有关闭时 统一设置false 为true时mp自动判断不处理
        if (!ConvertUtil.toBoolean(flag)) {
            FieldUtil.setFieldValue(tableInfo, "withLogicDelete", false);
        }
    }

}
