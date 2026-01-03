package com.simonvonxcvii.turing.resource.server.enums

/**
 * 文件类型枚举
 *
 * @author Simon Von
 * @since 2023/4/3 11:05
 */
enum class FileTypeEnum(val value: String) {
    /**
     * 信息采集
     */
    INFORMATION_COLLECTION("信息采集"),

    /**
     * 调查布点
     */
    SURVEY_LAYOUT("信息采集"),

    /**
     * 采样调查
     */
    PLOT_SAMPLING_SURVEY("信息采集"),

    /**
     * 样品检测
     */
    SAMPLE_TESTING("信息采集"),

    /**
     * 未知
     */
    UNKNOWN("unknown"),
    ;

    companion object {
        private val VALUES = entries.toTypedArray()

        @JvmStatic
        fun getValueByOrdinal(ordinal: Int): String? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum.value
            return null
        }

        @JvmStatic
        fun getOrdinalByValue(value: String): Int? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum.ordinal
            return null
        }

        @JvmStatic
        fun getEnumByOrdinal(ordinal: Int): FileTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return null
        }

        @JvmStatic
        fun getEnumByValue(value: String): FileTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum
            return null
        }
    }
}
