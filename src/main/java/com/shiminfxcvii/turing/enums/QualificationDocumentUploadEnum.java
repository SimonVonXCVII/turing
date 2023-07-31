package com.shiminfxcvii.turing.enums;


import lombok.Getter;

import java.util.Objects;

@Getter
public enum QualificationDocumentUploadEnum {
    DSC(0, "待上传"),
    YZC(1, "已暂存"),
    YTJ(2, "已提交"),
    ;

    private static final QualificationDocumentUploadEnum[] VALUES = values();

    private final Integer value;

    private final String desc;

    QualificationDocumentUploadEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDescByValue(Integer value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (QualificationDocumentUploadEnum obj : VALUES)
            if (Objects.equals(obj.getValue(), value))
                return obj.getDesc();

        return "";
    }

    public static Integer getValueByDesc(String desc) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (QualificationDocumentUploadEnum obj : VALUES)
            if (Objects.equals(obj.getDesc(), desc))
                return obj.getValue();
        return -1;
    }

    public static QualificationDocumentUploadEnum[] getValues() {
        return values();
    }

}