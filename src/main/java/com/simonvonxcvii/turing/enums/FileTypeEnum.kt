package com.simonvonxcvii.turing.enums

import lombok.Getter

/**
 * 文件类型枚举
 *
 * @author Simon Von
 * @since 2023/4/3 11:05
 */
@Getter
enum class FileTypeEnum(val desc: String) {
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

    companion object {
        private val VALUES = entries.toTypedArray()

        fun getValueByOrdinal(ordinal: Int?): String {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES) if (anEnum.ordinal == ordinal) return anEnum.desc

            return ""
        }

        @JvmStatic
        fun getByOrdinal(ordinal: Int?): FileTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES) if (anEnum.ordinal == ordinal) return anEnum

            return null
        }
    }
}
