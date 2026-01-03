package com.simonvonxcvii.turing.resource.server.enums

/**
 * 菜单徽标类型枚举
 *
 * @author Simon Von
 * @since 12/16/25 12:42 AM
 */
enum class MenuBadgeVariantsEnum(val value: String) {
    /**
     * default
     */
    DEFAULT("default"),

    /**
     * destructive
     */
    DESTRUCTIVE("destructive"),

    /**
     * primary
     */
    PRIMARY("primary"),

    /**
     * success
     */
    SUCCESS("success"),

    /**
     * warning
     */
    WARNING("warning"),

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
        fun getEnumByOrdinal(ordinal: Int): MenuBadgeVariantsEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return UNKNOWN
        }

        @JvmStatic
        fun getEnumByValue(value: String): MenuBadgeVariantsEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum
            return UNKNOWN
        }
    }
}
