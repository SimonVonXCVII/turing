package com.simonvonxcvii.turing.enums

/**
 * 单位类型枚举类
 *
 * @author Simon Von
 * @since 12/30/2022 6:46 PM
 */
enum class OrganizationTypeEnum(val desc: String) {
    PLATFORM("平台管理单位"),
    ADMINISTRATION("行政管理单位"),
    TECHNOLOGY_LEAD("技术牵头单位"),
    BUSINESS_TECHNOLOGY("业务技术单位"),
    EXPERT_CONSULTATION("专家咨询单位"),
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
        fun getByOrdinal(ordinal: Int): OrganizationTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return null
        }

        @JvmStatic
        fun getByDesc(desc: String): OrganizationTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.desc == desc)
                    return anEnum
            return null
        }
    }
}
