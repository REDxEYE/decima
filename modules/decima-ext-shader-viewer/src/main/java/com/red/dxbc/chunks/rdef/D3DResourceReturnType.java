package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumValue;

public enum D3DResourceReturnType implements EnumValue {
    UNORM,
    SNORM,
    SINT,
    UINT,
    FLOAT,
    MIXED,
    DOUBLE,
    CONTINUED;

    @Override
    public int value() {
        return ordinal();
    }
}
