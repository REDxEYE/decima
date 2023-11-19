package com.red.dxbc.chunks.shdr.opcodes.flowControl;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Statement;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class Switch extends Opcode {
    public final OperandToken0 input;

    public Switch(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        input = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "switch %s".formatted(input);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Statement("switch(%s)", input.toExpression(shader, -1)),
            new Statement("{")
        );
    }


}
