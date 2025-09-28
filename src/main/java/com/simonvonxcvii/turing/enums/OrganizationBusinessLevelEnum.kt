package com.simonvonxcvii.turing.enums

/**
 * 单位业务级别枚举类
 *
 * @author Simon Von
 * @since 1/4/2023 5:26 PM
 */
enum class OrganizationBusinessLevelEnum(val desc: String) {
    /**
     * 省级
     */
    PROVINCE("省级"),

    /**
     * 市级
     */
    CITY("市级"),

    /**
     * 县级
     */
    DISTRICT("县级"),
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
        fun getByOrdinal(ordinal: Int): OrganizationBusinessLevelEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return null
        }

        @JvmStatic
        fun getByDesc(desc: String): OrganizationBusinessLevelEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.desc == desc)
                    return anEnum
            return null
        }
    }
}
