package com.simonvonxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 单位类型枚举类
 *
 * @author SimonVonXCVII
 * @since 12/30/2022 6:46 PM
 */
@Getter
public enum OrganizationTypeEnum {

    PLATFORM("平台管理单位"),
    ADMINISTRATION("行政管理单位"),
    TECHNOLOGY_LEAD("技术牵头单位"),
    BUSINESS_TECHNOLOGY("业务技术单位"),
    EXPERT_CONSULTATION("专家咨询单位"),
    ;

    private static final OrganizationTypeEnum[] VALUES = values();

    private final String desc;

    OrganizationTypeEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (OrganizationTypeEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return "";
    }

}
