package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 审核状态枚举
 */
@Getter
public enum PointCheckStatusEnum {
    DSH(0, "待审核"),
    ZJTG(1, "直接通过"),
    JYXGWS(2, "建议修改完善"),
    JYXGCS(3, "建议修改重审"),
    XGWSFH(4, "修改完善复核"),
    XGCSFH(5, "修改重审复核"),
    WSFHTG(6, "完善后复核通过"),
    CSFHTG(7, "重审后复核通过"),
    BDDWTJ(8, "布点单位提交"),
    BDDWWSHTJ(9, "布点单位完善后提交"),
    BDDWCSHTJ(10, "布点单位重审后提交"),
    ;

    private static final PointCheckStatusEnum[] VALUES = values();

    private final Integer value;

    private final String desc;

    PointCheckStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDescByValue(Integer value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (PointCheckStatusEnum pointCheckStatusEnum : VALUES)
            if (Objects.equals(pointCheckStatusEnum.getValue(), value))
                return pointCheckStatusEnum.getDesc();

        return "";
    }

}