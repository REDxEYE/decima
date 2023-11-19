package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumValue;

public enum D3DShaderVariableClass implements EnumValue {
    SCALAR,
    VECTOR,
    MATRIX_ROWS,
    MATRIX_COLUMNS,
    OBJECT,
    STRUCT,
    INTERFACE_CLASS,
    INTERFACE_POINTER;

    @Override
    public int value() {
        return ordinal();
    }
}
