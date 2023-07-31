package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum QualificationDocumentCheckStageEnum {
    DWTJ(0, "单位提交"),
    SJSH(1, "省级审核"),
    GJCC(2, "国家抽查"),
    ;

    private static final QualificationDocumentCheckStageEnum[] VALUES = values();

    private final Integer value;

    private final String desc;

    QualificationDocumentCheckStageEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDescByValue(Integer value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (QualificationDocumentCheckStageEnum qualification : VALUES) {
            if (Objects.equals(qualification.getValue(), value)) {
                return qualification.getDesc();
            }
        }

        return "";
    }

    public static Integer getValueByDesc(String desc) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (QualificationDocumentCheckStageEnum qualification : VALUES) {
            if (Objects.equals(qualification.getDesc(), desc)) {
                return qualification.getValue();
            }
        }
        return -1;
    }

    public static QualificationDocumentCheckStageEnum[] getValues() {
        return values();
    }

}