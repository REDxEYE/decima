package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum IndexRepresentation implements EnumValue {
    IMMEDIATE32,
    IMMEDIATE64,
    RELATIVE,
    IMMEDIATE32_PLUS_RELATIVE,
    IMMEDIATE64_PLUS_RELATIVE;

    @Override
    public int value() {
        return ordinal();
    }
}
