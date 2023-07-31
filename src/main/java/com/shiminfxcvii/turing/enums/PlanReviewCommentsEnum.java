package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 方案审核意见枚举
 *
 * @author ShiminFXCVII
 * @since 2023/4/12 19:19
 */
@Getter
public enum PlanReviewCommentsEnum implements IEnum<Integer> {

    STAY_AUDIT("待审核"),
    PASS("直接通过"),
    BACK_PERFECT("退回完善"),
    PERFECT_REVIEW("完善待复核"),
    PERFECT_REVIEW_PASS("完善后复核通过"),
    BACK_RETRIAL("退回重审"),
    RETRIAL_REVIEW("重审待复核"),
    RETRIAL_REVIEW_PASS("重审后复核通过"),
    ;

    private static final PlanReviewCommentsEnum[] VALUES = values();

    private final String desc;

    PlanReviewCommentsEnum(String desc) {
        this.desc = desc;
    }

    public static String getValueByOrdinal(Integer ordinal) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (PlanReviewCommentsEnum anEnum : VALUES)
            if (Objects.equals(anEnum.ordinal(), ordinal))
                return anEnum.getDesc();

        return "";
    }

    /**
     * 枚举数据库存储值
     */
    @Override
    public Integer getValue() {
        return this.ordinal();
    }

}