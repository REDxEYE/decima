package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumValue;

public enum ProgramType implements EnumValue {
    VERTEX(0xFFFE),
    FRAGMENT(0xFFFF);

    private final int value;

    ProgramType(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
