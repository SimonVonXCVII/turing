package com.simonvonxcvii.turing.enums

import lombok.Getter

/**
 * 任务状态枚举
 *
 * @author Simon Von
 * @since 2023/4/1 18:11
 */
@Getter
enum class AssignmentStateEnum(val desc: String) {
    UNASSIGNED("未分配"),
    ASSIGNED("已分配"),
    WITHDRAWN("已撤回"),
    ;

    companion object {
        private val VALUES = entries.toTypedArray()

        fun getValueByOrdinal(ordinal: Int?): String {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES) if (anEnum.ordinal == ordinal) return anEnum.desc

            return ""
        }
    }
}
