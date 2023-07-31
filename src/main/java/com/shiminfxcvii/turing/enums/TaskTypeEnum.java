package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

import java.util.Objects;

/**
 * 任务类型枚举
 *
 * @author tyro
 * @author ShiminFXCVII
 */
@Getter
public enum TaskTypeEnum {
    INFORMATION_COLLECTION("信息采集", 0),
    SAMPLE_SURVEYS("采样调查", 1),
    HIDDEN_DANGER_INVESTIGATION("隐患排查", 2),
    ANALYZE_EVALUATE("分析评价", 3),
    RISK_ASSESSMENT("风险评估", 4);

    private static final TaskTypeEnum[] VALUES = values();
    private final String desc;
    @EnumValue
    private final Integer value;

    TaskTypeEnum(String desc, Integer value) {
        this.desc = desc;
        this.value = value;
    }

    public static TaskTypeEnum getByValue(Integer value) {
        for (TaskTypeEnum anEnum : VALUES) {
            if (Objects.equals(anEnum.getValue(), value)) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getDescByValue(Integer value) {
        for (TaskTypeEnum anEnum : VALUES) {
            if (Objects.equals(anEnum.getValue(), value)) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

    public static Integer getValueByDesc(String desc) {
        for (TaskTypeEnum anEnum : VALUES) {
            if (Objects.equals(anEnum.getDesc(), desc)) {
                return anEnum.getValue();
            }
        }
        return null;
    }

}