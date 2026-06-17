package com.scs.query;

import lombok.Data;

/**
 * 分页查询基础类
 */
@Data
public class PageQuery {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式：asc/desc
     */
    private String orderType = "desc";
}
