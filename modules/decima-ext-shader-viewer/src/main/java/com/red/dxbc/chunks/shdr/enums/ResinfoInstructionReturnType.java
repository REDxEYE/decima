package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum ResinfoInstructionReturnType implements EnumValue {
    D3D10_SB_RESINFO_INSTRUCTION_RETURN_FLOAT,
    D3D10_SB_RESINFO_INSTRUCTION_RETURN_RCPFLOAT,
    D3D10_SB_RESINFO_INSTRUCTION_RETURN_UINT;

    @Override
    public int value() {
        return ordinal();
    }
}
