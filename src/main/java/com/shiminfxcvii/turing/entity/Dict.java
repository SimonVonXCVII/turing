package com.shiminfxcvii.turing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-30 12:49:40
 */
@Accessors(chain = true)
@Getter
@Setter
@TableName("soil_dict")
public class Dict extends BaseEntity {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "soil_dict";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = INDEX + ":";

    /**
     * 字典名称
     */
    public String name;

    /**
     * 字典值
     */
    public String value;

    /**
     * 字典类型
     */
    public String type;

    /**
     * 上级 id
     */
    public String pid;

    /**
     * 说明
     */
    public String description;

    /**
     * 状态
     */
    public String status;

    /**
     * 排序
     */
    public Integer sort;
}