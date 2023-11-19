package com.red.dxbc.chunks.shdr.opcodes.floatOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public final class Multiply extends FloatMathOperation {
    public Multiply(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);

    }

    @Override
    public String toString() {
        return "mul %s, %s, %s".formatted(output, input0, input1);
    }

    @Override
    protected Expression innerExpression(@NotNull DXBC shader) {
        return new Expression("%s * %s",
            input0.toExpression(shader, output.componentSelection),
            input1.toExpression(shader, output.componentSelection)
        );
    }

}
