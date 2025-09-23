package com.simonvonxcvii.turing.enums

/**
 * 单位业务类型枚举类
 *
 * @author Simon Von
 * @since 1/4/2023 5:26 PM
 */
enum class OrganizationBusinessStateEnum(val desc: String) {
    AWAITING_CHECK("待审核"),
    PASSES("已通过"),
    RETURNED("已退回"),
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
    }
}
