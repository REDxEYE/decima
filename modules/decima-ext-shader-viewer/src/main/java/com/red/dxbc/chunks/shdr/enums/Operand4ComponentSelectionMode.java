package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum Operand4ComponentSelectionMode implements EnumValue {
    OPERAND_4_COMPONENT_MASK_MODE,  // mask 4 components
    OPERAND_4_COMPONENT_SWIZZLE_MODE,  // swizzle 4 components
    OPERAND_4_COMPONENT_SELECT_1_MODE; // select 1 of 4 components

    @Override
    public int value() {
        return ordinal();
    }
}
