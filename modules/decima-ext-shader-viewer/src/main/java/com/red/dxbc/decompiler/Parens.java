package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.Objects;

public class Parens implements Element {

    public Element element;

    public Parens(@NotNull Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return '(' + element.toString() + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parens parens = (Parens) o;
        return element.equals(parens.element);
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
        return this.element.inline(element);
    }
}
