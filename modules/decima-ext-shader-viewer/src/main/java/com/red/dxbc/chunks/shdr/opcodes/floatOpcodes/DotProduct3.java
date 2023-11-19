package com.red.dxbc.chunks.shdr.opcodes.floatOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.ComponentSelection;
import com.red.dxbc.chunks.shdr.enums.ComponentName;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Function;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public class DotProduct3 extends DotProduct2 {
    public DotProduct3(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);

    }

    @Override
    public String toString() {
        return "dp3 %s, %s, %s".formatted(output, input0, input1);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        Function function = new Function("dot",
            input0.toExpression(shader, new ComponentSelection(List.of(ComponentName.x, ComponentName.y, ComponentName.z))),
            input1.toExpression(shader, new ComponentSelection(List.of(ComponentName.x, ComponentName.y, ComponentName.z)))
        );
        if (isSaturated()) {
            function = new Function("saturate", function);
        }
        return List.of(new Assignment(output.toExpression(shader), function));
    }

}
