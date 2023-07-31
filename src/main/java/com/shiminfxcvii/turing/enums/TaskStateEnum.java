package com.shiminfxcvii.turing.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 任务状态枚举
 *
 * @author ShiminFXCVII
 * @since 2023/4/3 21:17
 */
@Getter
public enum TaskStateEnum implements IEnum<Integer> {

    NOT_STARTED("未开始"), IN_PROGRESS("进行中"), COMPLETED("已完成");


    private static final TaskStateEnum[] VALUES = values();
    private final String desc;

    TaskStateEnum(String desc) {
        this.desc = desc;
    }

    public static TaskStateEnum getByOrdinal(Integer ordinal) {
        for (TaskStateEnum anEnum : VALUES) {
            if (Objects.equals(anEnum.getValue(), ordinal)) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getDescByOrdinal(Integer ordinal) {
        for (TaskStateEnum anEnum : VALUES) {
            if (Objects.equals(anEnum.getValue(), ordinal)) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

    public static Integer getValueByDesc(String desc) {
        for (TaskStateEnum anEnum : VALUES) {
            if (Objects.equals(anEnum.getDesc(), desc)) {
                return anEnum.ordinal();
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return this.ordinal();
    }

}