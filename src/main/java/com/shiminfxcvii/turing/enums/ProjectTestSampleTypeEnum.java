package com.shiminfxcvii.turing.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum ProjectTestSampleTypeEnum {

    SOILED("固体废物", "D007-004"),
    WASTE_WATER("酸性废水", "D007-005"),
    BOTTOM_MUD("底泥", "D007-006"),
    SOIL("土壤", "D007-007"),
    IR_WATER("灌溉水", "D007-008"),
    FARM("农产品", "D007-009"),
    UNDER_WATER("地表水", "D007-010");

    private static final ProjectTestSampleTypeEnum[] VALUES = values();

    private final String name;

    private final String value;

    ProjectTestSampleTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String getNameByValue(String value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (ProjectTestSampleTypeEnum projectTestSampleTypeEnum : VALUES)
            if (Objects.equals(projectTestSampleTypeEnum.getValue(), value))
                return projectTestSampleTypeEnum.getName();

        return "";
    }

    public static String getValueByName(String name) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (ProjectTestSampleTypeEnum projectTestSampleTypeEnum : VALUES)
            if (Objects.equals(projectTestSampleTypeEnum.getName(), name))
                return projectTestSampleTypeEnum.getValue();

        return "";
    }
}