package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumValue;

public enum D3D11CBufferType implements EnumValue {
    CBUFFER(0),
    TBUFFER(1),
    INTERFACE_POINTERS(2),
    RESOURCE_BIND_INFO(3);

    private final int value;

    D3D11CBufferType(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
