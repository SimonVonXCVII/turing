package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum ProjectTestAuditStatusEnum {
    DSH(0, "待审核"),

    TG(1, "已生效"),

    WTG(2, "未通过");

    private static final ProjectTestAuditStatusEnum[] VALUES = values();

    private final Integer value;

    private final String desc;

    ProjectTestAuditStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDescByValue(Integer value) {
        for (ProjectTestAuditStatusEnum projectTestAuditStatusEnum : VALUES)
            if (Objects.equals(projectTestAuditStatusEnum.getValue(), value))
                return projectTestAuditStatusEnum.getDesc();

        return "";
    }

}