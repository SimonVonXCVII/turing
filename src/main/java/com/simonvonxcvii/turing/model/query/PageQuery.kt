package com.simonvonxcvii.turing.model.query

/**
 * 数据分页参数
 *
 * @author Simon Von
 * @since 2023/4/3 10:52
 */
open class PageQuery(
    /**
     * 当前页数
     */
    var page: Int? = null,

    /**
     * 当前页数大小
     */
    var pageSize: Int? = null
)
