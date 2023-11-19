package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum InterpolationMode implements EnumValue {
    UNDEFINED,
    CONSTANT,
    LINEAR,
    LINEAR_CENTROID,
    LINEAR_NOPERSPECTIVE,
    LINEAR_NOPERSPECTIVE_CENTROID,
    LINEAR_SAMPLE, // DX10.1
    LINEAR_NOPERSPECTIVE_SAMPLE; // DX10.1


    @Override
    public int value() {
        return ordinal();
    }
}
