package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 图斑类型/布点 | 调查对象类型枚举类
 *
 * @author shiminfxcvii
 * @since 2022/9/28 17:58 周三
 */
@Getter
public enum InvestigationObjectTypeEnum {

    GENERALLY("一般矿山"),
    EMPHASIS("重点矿山"),
    ;

    private static final InvestigationObjectTypeEnum[] VALUES = values();

    private final String value;

    InvestigationObjectTypeEnum(String value) {
        this.value = value;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (InvestigationObjectTypeEnum investigationObjectTypeEnum : VALUES)
            if (Objects.equals(investigationObjectTypeEnum.ordinal(), ordinal))
                return investigationObjectTypeEnum.getValue();

        return "";
    }

}