package com.simonvonxcvii.turing.resource.server.enums

/**
 * 菜单类型枚举
 *
 * @author Simon Von
 * @since 2025/9/25 11:30 PM
 */
enum class MenuTypeEnum(val value: String) {
    /**
     * 目录
     */
    CATALOG("catalog"),

    /**
     * 菜单
     */
    MENU("menu"),

    /**
     * 按钮
     */
    BUTTON("button"),

    /**
     * 内嵌
     */
    EMBEDDED("embedded"),

    /**
     * 外链
     */
    LINK("link"),

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
        fun getEnumByOrdinal(ordinal: Int): MenuTypeEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return UNKNOWN
        }

        @JvmStatic
        fun getEnumByValue(value: String): MenuTypeEnum {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.value == value)
                    return anEnum
            return UNKNOWN
        }
    }
}
