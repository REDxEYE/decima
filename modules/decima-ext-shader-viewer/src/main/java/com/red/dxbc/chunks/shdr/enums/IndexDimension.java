package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum IndexDimension implements EnumValue {
    INDEX_0D, // e.g. Position
    INDEX_1D, // Most common.  e.g. Temp registers.
    INDEX_2D, // e.g. Geometry Program Input registers.
    INDEX_3D; // 3D rarely if ever used.

    @Override
    public int value() {
        return ordinal();
    }
}
