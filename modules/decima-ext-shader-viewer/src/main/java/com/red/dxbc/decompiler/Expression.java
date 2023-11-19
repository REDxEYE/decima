package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Expression implements Element {
    public final String template;
    public List<Element> arguments;

    public Expression(String template, @NotNull Element... arguments) {
        this.template = template;
        this.arguments = new ArrayList<>(List.of(arguments));
    }

    public Expression(String template, @NotNull ArrayList<Element> arguments) {
        this.template = template;
        this.arguments = arguments;
    }


    @Override
    public String toString() {
        return template.formatted(arguments.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return template.equals(that.template) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, arguments);
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
            final Element inlineElement = arguments.get(i);
            if (inlineElement.equals(element.outputToken)) {
                arguments.set(i, wrap(arguments.get(i), element.inputToken));
                return true;
            }
            if (inlineElement.inline(element)) {
                return true;
            }
        }
        return false;
    }
}
