package com.red.dxbc.chunks.xsgn;

import com.shade.platform.model.util.EnumValue;

public enum D3DRegisterComponentType implements EnumValue {
    UNKNOWN(0),
    UINT(1),
    SINT(2),
    FLOAT(3),
    D3D10_UNKNOWN(4),
    D3D10_UINT(5),
    D3D10_SINT(6),
    D3D10_FLOAT(7);

    private final int value;

    D3DRegisterComponentType(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
