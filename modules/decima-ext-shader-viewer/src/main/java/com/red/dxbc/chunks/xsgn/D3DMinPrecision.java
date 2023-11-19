package com.red.dxbc.chunks.xsgn;

import com.shade.platform.model.util.EnumValue;

public enum D3DMinPrecision implements EnumValue {
    MIN_PRECISION_DEFAULT(0),
    MIN_PRECISION_FLOAT_16(1),
    MIN_PRECISION_FLOAT_2_8(2),
    MIN_PRECISION_RESERVED(3),
    MIN_PRECISION_SINT_16(4),
    MIN_PRECISION_UINT_16(5),
    MIN_PRECISION_ANY_16(0xf0),
    MIN_PRECISION_ANY_10(0xf1);

    private final int value;

    D3DMinPrecision(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
