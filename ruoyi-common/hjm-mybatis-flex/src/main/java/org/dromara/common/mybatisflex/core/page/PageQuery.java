package org.dromara.common.mybatisflex.core.page;

import cn.hutool.v7.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.sql.SqlUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询实体类
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc;

    /**
     * 当前记录起始索引 默认值
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 每页显示记录数 默认值 默认查全部
     */
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    /**
     * 构建分页对象
     */
    public <T> Page<T> build() {
        Integer pageNum = ObjUtil.defaultIfNull(getPageNum(), DEFAULT_PAGE_NUM);
        Integer pageSize = ObjUtil.defaultIfNull(getPageSize(), DEFAULT_PAGE_SIZE);
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        return new Page<>(pageNum, pageSize);
    }

    /**
     * 构建排序
     * <p>
     * 支持的用法如下:
     * {isAsc:"asc",orderByColumn:"id"} order by id asc
     * {isAsc:"asc",orderByColumn:"id,createTime"} order by id asc,create_time asc
     * {isAsc:"desc",orderByColumn:"id,createTime"} order by id desc,create_time desc
     * {isAsc:"asc,desc",orderByColumn:"id,createTime"} order by id asc,create_time desc
     */
    public void applyOrder(QueryWrapper wrapper) {
        if (ObjUtil.isNull(wrapper) || StringUtils.isBlank(orderByColumn) || StringUtils.isBlank(isAsc)) {
            return;
        }
        String orderBy = SqlUtil.escapeOrderBySql(orderByColumn);
        orderBy = StringUtils.toUnderScoreCase(orderBy);

        // 兼容前端排序类型
        isAsc = StringUtils.replaceEach(isAsc, new String[]{"ascending", "descending"}, new String[]{"asc", "desc"});

        String[] orderByArr = orderBy.split(StringUtils.SEPARATOR);
        String[] isAscArr = isAsc.split(StringUtils.SEPARATOR);
        if (isAscArr.length != 1 && isAscArr.length != orderByArr.length) {
            throw new ServiceException("排序参数有误");
        }

        for (int i = 0; i < orderByArr.length; i++) {
            String orderByStr = orderByArr[i];
            String isAscStr = isAscArr.length == 1 ? isAscArr[0] : isAscArr[i];
            if ("asc".equals(isAscStr)) {
                wrapper.orderBy(orderByStr, true);
            } else if ("desc".equals(isAscStr)) {
                wrapper.orderBy(orderByStr, false);
            } else {
                throw new ServiceException("排序参数有误");
            }
        }
    }

    @JsonIgnore
    public Integer getFirstNum() {
        return (pageNum - 1) * pageSize;
    }

    public PageQuery(Integer pageSize, Integer pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

}
