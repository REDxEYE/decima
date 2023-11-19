package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum SamplerMode implements EnumValue {
    MODE_DEFAULT,
    MODE_COMPARISON,
    MODE_MONO;

    @Override
    public int value() {
        return ordinal();
    }
}
