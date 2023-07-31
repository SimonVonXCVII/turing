package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

/**
 * 质控类型枚举类
 *
 * @author ShiminFXCVII
 * @since 1/4/2023 5:26 PM
 */
@Getter
public enum OrganizationBusinessQualityControlTypeEnum implements IEnum<Integer> {

    INFORMATION_COLLECTION_QC("信息采集质控"),
    DISTRIBUTE_QC("布点质控"),
    SAMPLING_QC("采样质控"),
    SAMPLE_TESTING_QC("样品检测质控"),
    ;

    private static final OrganizationBusinessQualityControlTypeEnum[] VALUES = values();

    private final String desc;

    OrganizationBusinessQualityControlTypeEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (OrganizationBusinessQualityControlTypeEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return null;
    }

    public static Optional<OrganizationBusinessQualityControlTypeEnum> getEnumByDesc(String desc) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (OrganizationBusinessQualityControlTypeEnum anEnum : VALUES)
            if (Objects.equals(anEnum.getDesc(), desc))
                return Optional.of(anEnum);

        return Optional.empty();
    }

    /**
     * 枚举数据库存储值
     */
    @Override
    public Integer getValue() {
        return this.ordinal();
    }

}