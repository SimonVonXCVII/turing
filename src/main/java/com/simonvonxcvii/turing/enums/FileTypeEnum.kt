package com.simonvonxcvii.turing.enums

/**
 * 文件类型枚举
 *
 * @author Simon Von
 * @since 2023/4/3 11:05
 */
enum class FileTypeEnum(val desc: String) {
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
    ;

    companion object {
        private val VALUES = entries.toTypedArray()

        @JvmStatic
        fun getDescByOrdinal(ordinal: Int): String? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum.desc
            return null
        }

        @JvmStatic
        fun getOrdinalByDesc(desc: String): Int? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.desc == desc)
                    return anEnum.ordinal
            return null
        }

        @JvmStatic
        fun getByOrdinal(ordinal: Int): FileTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return null
        }

        @JvmStatic
        fun getByDesc(desc: String): FileTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.desc == desc)
                    return anEnum
            return null
        }
    }
}
