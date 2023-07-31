package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum QualificationDocumentCheckEnum {
    DSH(0, "待审核"),
    WTG(1, "未通过"),
    YTG(2, "已通过"),
    YTJ(3, "已提交"),//用于审核记录中
    ;

    private static final QualificationDocumentCheckEnum[] VALUES = values();

    private final Integer value;

    private final String desc;

    QualificationDocumentCheckEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDescByValue(Integer value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (QualificationDocumentCheckEnum qualification : VALUES) {
            if (Objects.equals(qualification.getValue(), value)) {
                return qualification.getDesc();
            }
        }

        return "";
    }

    public static Integer getValueByDesc(String desc) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (QualificationDocumentCheckEnum qualification : VALUES) {
            if (Objects.equals(qualification.getDesc(), desc)) {
                return qualification.getValue();
            }
        }
        return -1;
    }

    public static QualificationDocumentCheckEnum[] getValues() {
        return values();
    }

}