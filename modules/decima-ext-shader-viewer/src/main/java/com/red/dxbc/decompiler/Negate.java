package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

public class Negate implements Element {

    public Element element;

    public Negate(@NotNull Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return '-' + element.toString();
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return element.getOperandUseCount(operand);
    }

    @Override
    public boolean inline(Assignment element) {
        if (this.element.equals(element.outputToken)) {
            this.element = wrap(this.element, element.inputToken);
            return true;
        }
        return this.element.inline(element);
    }
}
