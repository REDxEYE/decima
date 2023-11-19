package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.Objects;

public class VariableArrayDeclaration implements Element {
    public final String type;
    public final String name;
    public final int length;

    public VariableArrayDeclaration(@NotNull String type, @NotNull String name, int length) {
        this.type = type;
        this.name = name;
        this.length = length;
    }

    @Override
    public String toString() {
        return "%s[%d] %s".formatted(type, length, name);
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableArrayDeclaration that = (VariableArrayDeclaration) o;
        return length == that.length && type.equals(that.type) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, length);
    }
}
