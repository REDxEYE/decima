package com.red.dxbc.chunks.shdr.enums;

import com.shade.platform.model.util.EnumValue;

public enum OperandNumComponents implements EnumValue {
    OPERAND_0_COMPONENT,
    OPERAND_1_COMPONENT,
    OPERAND_4_COMPONENT,
    OPERAND_N_COMPONENT; // unused for now


    @Override
    public int value() {
        return ordinal();
    }

    public int componentCount() {
        return switch (this) {

            case OPERAND_0_COMPONENT -> 0;
            case OPERAND_1_COMPONENT -> 1;
            case OPERAND_4_COMPONENT -> 4;
            case OPERAND_N_COMPONENT -> {
                throw new IllegalStateException("Should not even be hit");
            }
        };
    }
}
