package com.red.dxbc.decompiler;

public interface Element {

    boolean equals(Object o);

    int hashCode();

    int getOperandUseCount(Operand operand);

    default boolean inline(Assignment element) {
        return false;
    }

    default Element wrap(Element oldOperand, Element element) {
        if (!(oldOperand instanceof Operand)) {
            throw new IllegalArgumentException("AAAAAAAAAA");
        }
        if (element instanceof Function) {
            return new InlineResult(oldOperand.toString(), element);
        } else {
            return new Parens(new InlineResult(oldOperand.toString(), element));
        }
    }
}
