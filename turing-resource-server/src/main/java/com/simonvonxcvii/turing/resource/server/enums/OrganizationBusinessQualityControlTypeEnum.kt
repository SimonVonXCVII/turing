package com.simonvonxcvii.turing.resource.server.enums

/**
 * 质控类型枚举类
 *
 * @author Simon Von
 * @since 1/4/2023 5:26 PM
 */
enum class OrganizationBusinessQualityControlTypeEnum(val value: String) {
    INFORMATION_COLLECTION_QC("信息采集质控"),
    DISTRIBUTE_QC("布点质控"),
    SAMPLING_QC("采样质控"),
    SAMPLE_TESTING_QC("样品检测质控"),

    /**
     * 未知
     */
    UNKNOWN("unknown"),
    ;

    companion object {
        private val VALUES = entries.toTypedArray()

        @JvmStatic
        fun getValueByOrdinal(ordinal: Int): String {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum.value
            return UNKNOWN.value
        }

        @JvmStatic
        fun getOrdinalByValue(value: String): Int {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum.ordinal
            return UNKNOWN.ordinal
        }

        @JvmStatic
        fun getEnumByOrdinal(ordinal: Int): OrganizationBusinessQualityControlTypeEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return UNKNOWN
        }

        @JvmStatic
        fun getEnumByValue(value: String): OrganizationBusinessQualityControlTypeEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum
            return UNKNOWN
        }
    }
}
