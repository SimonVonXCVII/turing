package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

/**
 * 业务环节枚举类
 *
 * @author ShiminFXCVII
 * @since 1/4/2023 5:26 PM
 */
@Getter
public enum OrganizationBusinessBusinessLinksEnum implements IEnum<Integer> {

    MINE_INFORMATION_COLLECTION("矿山信息采集"),
    REGIONAL_SURVEY_LOCATIONS("区域调查布点"),
    AREA_SAMPLING_SURVEYS("区域采样调查"),
    LAND_PARCEL_INFORMATION_COLLECTION("地块信息采集"),
    PLOT_SURVEY_LAYOUT("地块调查布点"),
    PLOT_SAMPLING_SURVEY("地块采样调查"),
    SAMPLE_TESTING("样品检测"),
    DATA_ANALYSIS_EVALUATION("数据分析评价"),
    CONTAMINATION_RISK_ASSESSMENT("污染风险评估"),
    ;

    private static final OrganizationBusinessBusinessLinksEnum[] VALUES = values();

    private final String desc;

    OrganizationBusinessBusinessLinksEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (OrganizationBusinessBusinessLinksEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return null;
    }

    public static Optional<OrganizationBusinessBusinessLinksEnum> getEnumByDesc(String desc) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (OrganizationBusinessBusinessLinksEnum anEnum : VALUES)
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