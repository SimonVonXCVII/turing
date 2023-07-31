package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 监管对象分类枚举
 *
 * @author ShiminFXCVII
 * @since 2023/4/4 10:11
 */
@Getter
public enum ObjectManageLevelEnum implements IEnum<Integer> {

    REGIONAL_SOILS("区域土壤"),
    SHUT_DOWN_BUSINESSES("关停企业"),
    IN_PRODUCTION_ENTERPRISES("在产企业"),
    ;

    private static final ObjectManageLevelEnum[] VALUES = values();

    private final String desc;

    ObjectManageLevelEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (ObjectManageLevelEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return "";
    }

    /**
     * 枚举数据库存储值
     */
    @Override
    public Integer getValue() {
        return this.ordinal();
    }

}