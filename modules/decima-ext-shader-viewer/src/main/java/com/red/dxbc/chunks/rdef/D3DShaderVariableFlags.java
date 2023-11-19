package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumSetValue;

public enum D3DShaderVariableFlags implements EnumSetValue {
    USERPACKED(1),
    USED(2),
    INTERFACE_POINTER(4),
    INTERFACE_PARAMETER(8);

    public final int value;

    D3DShaderVariableFlags(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
