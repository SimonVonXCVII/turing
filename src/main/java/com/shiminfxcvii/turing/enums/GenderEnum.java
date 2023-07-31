package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 性别枚举
 *
 * @author ShiminFXCVII
 * @since 12/20/2022 10:51 AM
 */
@Getter
public enum GenderEnum implements IEnum<Integer> {

    FEMALE("女"),
    MALE("男"),
    ;

    private static final GenderEnum[] VALUES = values();

    private final String desc;

    GenderEnum(String desc) {
        this.desc = desc;
    }

    /**
     * 根据常量序数获取枚举
     *
     * @param ordinal 常量序数
     * @return 枚举
     * @author ShiminFXCVII
     * @since 12/30/2022 7:35 PM
     */
    public static GenderEnum getGenderByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (GenderEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum;

        return null;
    }

    /**
     * 根据常量序数获取描述值
     *
     * @param ordinal 常量序数
     * @return 描述值
     * @author ShiminFXCVII
     * @since 12/30/2022 7:36 PM
     */
    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (GenderEnum anEnum : VALUES)
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