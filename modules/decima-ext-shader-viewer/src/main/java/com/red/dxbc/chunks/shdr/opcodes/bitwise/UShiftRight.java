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

public final class UShiftRight extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input;
    public final OperandToken0 shift;

    public UShiftRight(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        input = new OperandToken0(buffer, true);
        shift = new OperandToken0(buffer, true);

    }

    @Override
    public String toString() {
        return "ushr %s, %s, %s".formatted(output, input, shift);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(new Assignment(
            output.toExpression(shader),
            new Expression("%s >> %s",
                input.toExpression(shader, output.componentSelection),
                shift.toExpression(shader, output.componentSelection)
            )
        ));
    }

}
