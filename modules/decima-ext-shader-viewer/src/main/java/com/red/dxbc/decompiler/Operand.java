package com.red.dxbc.decompiler;

import com.red.dxbc.chunks.shdr.enums.ComponentName;
import com.shade.util.Nullable;

import java.util.List;
import java.util.Objects;

public class Operand implements Element {
    public final String name;
    public List<ComponentName> components;

    public Operand(String name) {
        this.name = name;
        components = null;
    }

    public Operand(String name, @Nullable List<ComponentName> components) {
        this.name = name;
        this.components = components;
    }

    public boolean isCompatible(Operand other) {
        if (other.components.size() == 1) {
            for (ComponentName component : components) {
                if (component != other.components.get(0)) {
                    return false;
                }
            }
            return true;
        }
        return equals(other);
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder(name);
        if (components != null && components.size() > 0) {
            tmp.append('.');
            for (ComponentName component : components) {
                tmp.append(component.name());
            }
        }
        return tmp.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operand operand = (Operand) o;
        return name.equals(operand.name) && Objects.equals(components, operand.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, components);
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return operand.equals(this) ? 1 : 0;
    }
}
