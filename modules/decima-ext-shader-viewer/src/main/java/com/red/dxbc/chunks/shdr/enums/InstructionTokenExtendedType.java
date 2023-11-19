package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum InstructionTokenExtendedType implements EnumValue {
    Empty,
    SampleControls,
    ResourceDim,
    ResourceReturnType;

    @Override
    public int value() {
        return ordinal();
    }
}
