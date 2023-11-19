package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum ResourceDimension implements EnumValue {
    UNKNOWN,
    BUFFER,
    TEXTURE1D,
    TEXTURE2D,
    TEXTURE2DMS,
    TEXTURE3D,
    TEXTURECUBE,
    TEXTURE1DARRAY,
    TEXTURE2DARRAY,
    TEXTURE2DMSARRAY,
    TEXTURECUBEARRAY;

    @Override
    public int value() {
        return ordinal();
    }
}
