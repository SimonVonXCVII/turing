package com.simonvonxcvii.turing.enums

/**
 * 菜单类型枚举
 *
 * @author Simon Von
 * @since 2025/9/25 11:30 PM
 */
enum class MenuTypeEnum(val desc: String) {
    /**
     * 目录
     */
    DIRECTORY("目录"),

    /**
     * 菜单
     */
    ROUTE("菜单"),

    /**
     * 按钮
     */
    BUTTON("按钮"),
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
        fun getByOrdinal(ordinal: Int): MenuTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return null
        }

        @JvmStatic
        fun getByDesc(desc: String): MenuTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.desc == desc)
                    return anEnum
            return null
        }
    }
}
