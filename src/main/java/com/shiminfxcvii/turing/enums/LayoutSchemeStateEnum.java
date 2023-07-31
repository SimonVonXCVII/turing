package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 布点方案状态枚举
 *
 * @author ShiminFXCVII
 * @since 2023/4/4 17:45
 */
@Getter
public enum LayoutSchemeStateEnum implements IEnum<Integer> {

    UNPROCESSED("未处理"),
    IN_PROGRESS("进行中"),
    SUBMITTED("已提交"),
    ;

    private static final LayoutSchemeStateEnum[] VALUES = values();

    private final String desc;

    LayoutSchemeStateEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (LayoutSchemeStateEnum anEnum : VALUES)
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