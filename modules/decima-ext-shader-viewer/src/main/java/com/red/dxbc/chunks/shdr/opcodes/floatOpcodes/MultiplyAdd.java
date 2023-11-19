package com.red.dxbc.chunks.shdr.opcodes.floatOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public final class MultiplyAdd extends FloatMathOperation {
    public final OperandToken0 input2;


    public MultiplyAdd(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);
        input2 = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "mad %s, %s, %s, %s".formatted(output, input0, input1, input2);
    }

    @Override
    protected Expression innerExpression(@NotNull DXBC shader) {
        return new Expression("%s + %s * %s",
            input0.toExpression(shader, output.componentSelection),
            input1.toExpression(shader, output.componentSelection),
            input2.toExpression(shader, output.componentSelection)
        );
    }
}
