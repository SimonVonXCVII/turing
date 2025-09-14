package com.simonvonxcvii.turing.enums

import lombok.Getter
import java.util.*

/**
 * 业务环节枚举类
 *
 * @author Simon Von
 * @since 1/4/2023 5:26 PM
 */
@Getter
enum class OrganizationBusinessBusinessLinksEnum(val desc: String) {
    MINE_INFORMATION_COLLECTION("矿山信息采集"),
    REGIONAL_SURVEY_LOCATIONS("区域调查布点"),
    AREA_SAMPLING_SURVEYS("区域采样调查"),
    LAND_PARCEL_INFORMATION_COLLECTION("地块信息采集"),
    PLOT_SURVEY_LAYOUT("地块调查布点"),
    PLOT_SAMPLING_SURVEY("地块采样调查"),
    SAMPLE_TESTING("样品检测"),
    DATA_ANALYSIS_EVALUATION("数据分析评价"),
    CONTAMINATION_RISK_ASSESSMENT("污染风险评估"),
    ;

    companion object {
        private val VALUES = entries.toTypedArray()

        fun getValueByOrdinal(ordinal: Int?): String? {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES) if (anEnum.ordinal == ordinal) return anEnum.desc

            return null
        }

        @JvmStatic
        fun getEnumByDesc(desc: String?): Optional<OrganizationBusinessBusinessLinksEnum> {
            // Use cached VALUES instead of values() to prevent array allocation.
            for (anEnum in VALUES) if (anEnum.desc == desc) return Optional.of<OrganizationBusinessBusinessLinksEnum>(
                anEnum
            )

            return Optional.empty<OrganizationBusinessBusinessLinksEnum>()
        }
    }
}
