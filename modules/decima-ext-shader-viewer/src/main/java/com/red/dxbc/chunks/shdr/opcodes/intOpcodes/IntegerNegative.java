package com.red.dxbc.chunks.shdr.opcodes.intOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Negate;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class IntegerNegative extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input;


    public IntegerNegative(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        if (isSaturated()) {
            throw new IllegalStateException("Int math cannot be saturated");
        }
        output = new OperandToken0(buffer);
        input = new OperandToken0(buffer, true);
    }

    @Override
    public String toString() {
        return "ineg %s, %s".formatted(output, input);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Assignment(output.toExpression(shader),
                new Negate(input.toExpression(shader, output.componentSelection)
                )
            )
        );
    }

}
