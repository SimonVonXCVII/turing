package com.simonvonxcvii.turing.resource.server.enums

/**
 * 单位类型枚举类
 *
 * @author Simon Von
 * @since 12/30/2022 6:46 PM
 */
enum class OrganizationTypeEnum(val value: String) {
    PLATFORM("平台管理单位"),
    ADMINISTRATION("行政管理单位"),
    TECHNOLOGY_LEAD("技术牵头单位"),
    BUSINESS_TECHNOLOGY("业务技术单位"),
    EXPERT_CONSULTATION("专家咨询单位"),

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
        fun getEnumByOrdinal(ordinal: Int): OrganizationTypeEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return UNKNOWN
        }

        @JvmStatic
        fun getEnumByValue(value: String): OrganizationTypeEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum
            return UNKNOWN
        }
    }
}
