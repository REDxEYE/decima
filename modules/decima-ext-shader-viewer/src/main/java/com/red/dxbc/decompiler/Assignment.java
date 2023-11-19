package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.Objects;

public class Assignment implements Element {
    public Element outputToken;
    public Element inputToken;

    public Assignment(@NotNull Element outputToken, @NotNull Element inputTokens) {
        this.outputToken = outputToken;
        this.inputToken = inputTokens;
    }

    public String toString() {
        return "%s = %s".formatted(outputToken.toString(), inputToken.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment assignment = (Assignment) o;
        return outputToken.equals(assignment.outputToken) && inputToken.equals(assignment.inputToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputToken, inputToken);
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return inputToken.getOperandUseCount(operand);
    }

    @Override
    public boolean inline(Assignment element) {
        if (inputToken.equals(element.outputToken)) {
            inputToken = wrap((Operand) inputToken, element.inputToken);
            return true;
        }
        return inputToken.inline(element);
    }
}
