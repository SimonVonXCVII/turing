package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * 行政级别枚举
 *
 * @author ShiminFXCVII
 * @since 3/4/2023 8:01 PM
 */
@Getter
public enum AdministrativeLevelEnum implements IEnum<Integer> {
    /**
     * 国家级
     */
    COUNTRY("国家级"),
    /**
     * 省级
     */
    PROVINCE("省级"),
    /**
     * 市级
     */
    CITY("市级"),
    /**
     * 县级
     */
    DISTRICT("县级");

    private final String desc;

    AdministrativeLevelEnum(String desc) {
        this.desc = desc;
    }

    /**
     * 枚举数据库存储值
     */
    @Override
    public Integer getValue() {
        return this.ordinal();
    }
}