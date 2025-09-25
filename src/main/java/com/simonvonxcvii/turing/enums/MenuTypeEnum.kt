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

        fun getValueByOrdinal(ordinal: Int): String {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum.desc

            return ""
        }

        @JvmStatic
        fun getByOrdinal(ordinal: Int): MenuTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum

            return null
        }
    }
}
