package com.red.dxbc.chunks.shdr.opcodes.flowControl;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class SwitchCase extends Opcode {
    public final OperandToken0 input;

    public SwitchCase(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        input = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "case %s".formatted(input);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Expression("case(%s):", input.toExpression(shader, -1))
        );
    }


}
