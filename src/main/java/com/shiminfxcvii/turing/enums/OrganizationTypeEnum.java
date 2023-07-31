package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 单位类型枚举类
 *
 * @author ShiminFXCVII
 * @since 12/30/2022 6:46 PM
 */
@Getter
public enum OrganizationTypeEnum implements IEnum<Integer> {

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


    /**
     * 枚举数据库存储值
     */
    @Override
    public Integer getValue() {
        return this.ordinal();
    }

}