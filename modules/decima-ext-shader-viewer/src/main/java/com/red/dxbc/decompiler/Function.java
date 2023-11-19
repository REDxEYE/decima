package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Function implements Element {
    public final String name;
    public List<Element> arguments;

    public Function(String name) {
        this.name = name;
    }

    public Function(String name, @NotNull Element... arguments) {
        this.name = name;
        this.arguments = new ArrayList<>(List.of(arguments));
    }

    public Function(String name, @NotNull ArrayList<Element> arguments) {
        this.name = name;
        this.arguments = arguments;
    }


    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder(name);
        tmp.append('(');
        for (int i = 0; i < arguments.size(); i++) {
            tmp.append(arguments.get(i).toString());
            if (i != arguments.size() - 1) {
                tmp.append(", ");
            }
        }
        tmp.append(')');
        return tmp.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return name.equals(function.name) && arguments.equals(function.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        int useCount = 0;
        for (Element argument : arguments) {
            useCount += argument.getOperandUseCount(operand);
        }
        return useCount;
    }

    @Override
    public boolean inline(Assignment element) {
        for (int i = 0; i < arguments.size(); i++) {
            final Element inlineeElement = arguments.get(i);
            if (inlineeElement.equals(element.outputToken)) {
                arguments.set(i, wrap(arguments.get(i), element.inputToken));
                return true;
            }
            if (inlineeElement.inline(element)) {
                return true;
            }
        }
        return false;
    }
}
