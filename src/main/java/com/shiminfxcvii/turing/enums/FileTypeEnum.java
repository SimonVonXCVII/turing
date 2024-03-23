package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 文件类型枚举
 *
 * @author ShiminFXCVII
 * @since 2023/4/3 11:05
 */
@Getter
public enum FileTypeEnum {

    /**
     * 信息采集
     */
    INFORMATION_COLLECTION("Information Collection"),
    /**
     * 调查布点
     */
    SURVEY_LAYOUT("Survey Layout"),
    /**
     * 采样调查
     */
    PLOT_SAMPLING_SURVEY("Plot Sampling Survey"),
    /**
     * 样品检测
     */
    SAMPLE_TESTING("Sample Testing"),
    ;

    private static final FileTypeEnum[] VALUES = values();

    private final String desc;

    FileTypeEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (FileTypeEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return "";
    }

    public static FileTypeEnum getByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (FileTypeEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum;

        return null;
    }

}
