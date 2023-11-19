package com.red.dxbc.chunks.shdr.opcodes.bitwise;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class BitFieldInsert extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 width;
    public final OperandToken0 offset;
    public final OperandToken0 input0;
    public final OperandToken0 input1;

    public BitFieldInsert(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        width = new OperandToken0(buffer, true);
        offset = new OperandToken0(buffer, true);
        input0 = new OperandToken0(buffer, true);
        input1 = new OperandToken0(buffer, true);

    }

    @Override
    public String toString() {
        return "bfi %s, %s, %s, %s, %s".formatted(output, width, offset, input0, input1);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(new Assignment(
            output.toExpression(shader),
            new Expression("((%s << %s) & (((1 << %s)-1) << %s)) | (%s & ~(((1 << %s)-1) << %s))",
                input0.toExpression(shader, output.componentSelection),
                offset.toExpression(shader, output.componentSelection),
                width.toExpression(shader, output.componentSelection),
                offset.toExpression(shader, output.componentSelection),
                input1.toExpression(shader, output.componentSelection),
                width.toExpression(shader, output.componentSelection),
                offset.toExpression(shader, output.componentSelection)
                )
            ));
    }

}
