package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumSetValue;

public enum D3DShaderCBufferFlags implements EnumSetValue {
    D3D_CBF_USERPACKED(1),
    D3D10_CBF_USERPACKED(2),
    D3D_CBF_FORCE_DWORD(0x7fffffff);

    private final int value;

    D3DShaderCBufferFlags(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
