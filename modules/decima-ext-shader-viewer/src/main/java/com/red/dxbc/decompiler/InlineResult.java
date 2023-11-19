package com.red.dxbc.decompiler;

public class InlineResult implements Element {
    public final String varName;
    public Element replacement;

    public InlineResult(String varName, Element element) {
        this.varName = varName;
        this.replacement = element;
    }


    @Override
    // public String toString() {
    //     return " /* " + varName + "= */ " + replacement;
    // }
    public String toString() {
        return replacement.toString();
    }


    @Override
    public int getOperandUseCount(Operand operand) {
        return replacement.getOperandUseCount(operand);
    }

    @Override
    public boolean inline(Assignment element) {
        return replacement.inline(element);
    }
}
