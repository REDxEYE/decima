package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumValue;

public enum D3DSRVDimension implements EnumValue {
    UNKNOWN,
    BUFFER,
    TEXTURE1D,
    TEXTURE1DARRAY,
    TEXTURE2D,
    TEXTURE2DARRAY,
    TEXTURE2DMS,
    TEXTURE2DMSARRAY,
    TEXTURE3D,
    TEXTURECUBE,
    TEXTURECUBEARRAY,
    BUFFEREX;

    @Override
    public int value() {
        return ordinal();
    }
}
