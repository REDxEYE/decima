package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum ComponentName implements EnumValue {
    x,
    y,
    z,
    w;

    @Override
    public int value() {
        return ordinal();
    }
}
