package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum ProgramType implements EnumValue {
    PixelShader,
    VertexShader,
    GeometryShader,
    HullShader,
    DomainShader,
    ComputeShader;

    @Override
    public int value() {
        return ordinal();
    }
}
