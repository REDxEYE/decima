package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum OperandIndexDimension implements EnumValue {
    INDEX_0D,
    INDEX_1D,
    INDEX_2D,
    INDEX_3D;

    @Override
    public int value() {
        return ordinal();
    }
}
