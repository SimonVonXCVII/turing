package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 单位业务类型枚举类
 *
 * @author ShiminFXCVII
 * @since 1/4/2023 5:26 PM
 */
@Getter
public enum OrganizationBusinessStateEnum implements IEnum<Integer> {

    AWAITING_CHECK("待审核"),
    PASSES("已通过"),
    RETURNED("已退回"),
    ;

    private static final OrganizationBusinessStateEnum[] VALUES = values();

    private final String desc;

    OrganizationBusinessStateEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (OrganizationBusinessStateEnum anEnum : VALUES)
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