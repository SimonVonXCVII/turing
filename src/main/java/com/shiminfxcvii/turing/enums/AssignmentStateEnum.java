package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 任务状态枚举
 *
 * @author ShiminFXCVII
 * @since 2023/4/1 18:11
 */
@Getter
public enum AssignmentStateEnum {

    UNASSIGNED("未分配"),
    ASSIGNED("已分配"),
    WITHDRAWN("已撤回"),
    ;

    private static final AssignmentStateEnum[] VALUES = values();

    private final String desc;

    AssignmentStateEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (AssignmentStateEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return "";
    }

}
