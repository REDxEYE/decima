package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.EnumValue;

public enum D3DShaderInputType implements EnumValue {
    CBUFFER,
    TBUFFER,
    TEXTURE,
    SAMPLER,
    UAV_RWTYPED,
    STRUCTURED,
    UAV_RWSTRUCTURED,
    BYTEADDRESS,
    UAV_RWBYTEADDRESS,
    UAV_APPEND_STRUCTURED,
    UAV_CONSUME_STRUCTURED,
    UAV_RWSTRUCTURED_WITH_COUNTER,
    RTACCELERATIONSTRUCTURE,
    UAV_FEEDBACKTEXTURE;

    @Override
    public int value() {
        return ordinal();
    }
}
