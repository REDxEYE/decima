package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum ResourceReturnType implements EnumValue {
    VOID,
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
