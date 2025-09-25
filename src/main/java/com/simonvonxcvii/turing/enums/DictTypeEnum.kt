package com.simonvonxcvii.turing.enums

/**
 * 菜单类型枚举
 *
 * @author Simon Von
 * @since 2025/9/25 11:30 PM
 */
enum class DictTypeEnum(val desc: String) {
    /**
     * 地区
     */
    AREA("地区"),
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
        fun getByOrdinal(ordinal: Int): DictTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum

            return null
        }
    }
}
