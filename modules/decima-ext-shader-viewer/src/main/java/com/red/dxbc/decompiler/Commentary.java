package com.red.dxbc.decompiler;

import com.shade.util.NotNull;

import java.util.List;

public class Commentary implements Element {
    public final String template;

    public Commentary(String template) {
        this.template = template;
    }


    @Override
    public String toString() {
        return "// " + template;
    }


    @Override
    public int getOperandUseCount(Operand operand) {
        return 0;
    }
}
