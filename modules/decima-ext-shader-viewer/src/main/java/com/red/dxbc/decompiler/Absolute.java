package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.Objects;

public class Absolute implements Element {

    public Element element;

    public Absolute(@NotNull Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "abs(" + element.toString() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Absolute absolute = (Absolute) o;
        return element.equals(absolute.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return element.getOperandUseCount(operand);
    }

    @Override
    public boolean inline(Assignment element) {
        if (this.element.equals(element.outputToken)) {
            this.element = wrap((Operand) this.element, element.inputToken);
            return true;
        }
        return element.inline(element);
    }
}
