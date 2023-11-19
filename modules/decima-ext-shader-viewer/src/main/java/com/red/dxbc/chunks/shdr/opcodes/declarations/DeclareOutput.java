package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public class DeclareOutput extends Opcode {

    public final OperandToken0 operand;

    public DeclareOutput(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        operand = new OperandToken0(buffer);
    }

    @Override
    public String toString() {
        return "dcl_output %s".formatted(operand);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Output %s%s".formatted(operand.toExpression(shader, 0), operand.indices[0]))
        );
    }

}
