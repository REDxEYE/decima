package com.red.dxbc.decompiler;

public class Inlined implements Element {
    public final String template;

    public Inlined(String template) {
        this.template = template;
    }


    @Override
    public String toString() {
        return "// Inlined " + template;
    }


    @Override
    public int getOperandUseCount(Operand operand) {
        return 0;
    }
}
