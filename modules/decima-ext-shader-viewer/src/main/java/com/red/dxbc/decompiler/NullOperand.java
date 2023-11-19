package com.red.dxbc.decompiler;

public class NullOperand implements Element {

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return 0;
    }
}
