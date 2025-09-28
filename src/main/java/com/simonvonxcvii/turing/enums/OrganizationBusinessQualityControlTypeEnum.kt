package com.simonvonxcvii.turing.enums

/**
 * 质控类型枚举类
 *
 * @author Simon Von
 * @since 1/4/2023 5:26 PM
 */
enum class OrganizationBusinessQualityControlTypeEnum(val desc: String) {
    INFORMATION_COLLECTION_QC("信息采集质控"),
    DISTRIBUTE_QC("布点质控"),
    SAMPLING_QC("采样质控"),
    SAMPLE_TESTING_QC("样品检测质控"),
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
        fun getByOrdinal(ordinal: Int): OrganizationBusinessQualityControlTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.ordinal == ordinal)
                    return anEnum
            return null
        }

        @JvmStatic
        fun getByDesc(desc: String): OrganizationBusinessQualityControlTypeEnum? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES)
                if (anEnum.desc == desc)
                    return anEnum
            return null
        }
    }
}
