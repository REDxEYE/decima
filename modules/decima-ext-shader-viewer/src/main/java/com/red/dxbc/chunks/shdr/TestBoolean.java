package com.red.dxbc.chunks.shdr;

import com.shade.platform.model.util.EnumValue;

public enum TestBoolean implements EnumValue {
    TEST_ZERO, TEST_NONZERO;

    @Override
    public int value() {
        return ordinal();
    }
}
