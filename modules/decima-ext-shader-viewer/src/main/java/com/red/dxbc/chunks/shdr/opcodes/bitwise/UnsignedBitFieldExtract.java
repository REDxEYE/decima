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

public final class UnsignedBitFieldExtract extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input;
    public final OperandToken0 width;
    public final OperandToken0 offset;

    public UnsignedBitFieldExtract(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        input = new OperandToken0(buffer, true);
        width = new OperandToken0(buffer, true);
        offset = new OperandToken0(buffer, true);

    }

    @Override
    public String toString() {
        return "ubfe %s, %s, %s, %s".formatted(output, input, width, offset);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(new Assignment(
            output.toExpression(shader),
            new Expression("(%s >> %s) & ((1U << %s) - 1)",
                input.toExpression(shader, output.componentSelection),
                width.toExpression(shader, output.componentSelection),
                offset.toExpression(shader, output.componentSelection)
            )
        ));
    }

}
