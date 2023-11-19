package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.Objects;

public class VariableDeclaration implements Element {
    public final String type;
    public final String name;

    public VariableDeclaration(@NotNull String type, @NotNull String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(type, name);
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableDeclaration that = (VariableDeclaration) o;
        return type.equals(that.type) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}
