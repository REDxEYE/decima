package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum ConstantBufferAccessPattern implements EnumValue {
    IMMEDIATE_INDEXED,
    DYNAMIC_INDEXED;

    @Override
    public int value() {
        return ordinal();
    }
}
