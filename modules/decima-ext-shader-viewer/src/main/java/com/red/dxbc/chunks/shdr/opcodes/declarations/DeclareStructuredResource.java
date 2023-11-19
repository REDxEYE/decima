package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareStructuredResource extends Opcode {

    public final OperandToken0 operand;
    public final int stride;
    public final int space;

    public DeclareStructuredResource(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        operand = new OperandToken0(buffer);
        stride = buffer.getInt();
        space = buffer.getInt();


    }

    @Override
    public String toString() {
        return "dcl_resource_structured %s%s[%s:%s], %d, space=%d".formatted(operand.operandData, operand.indices[0], operand.indices[1], operand.indices[2], stride, space);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Structured Resource %s, stride = %d".formatted(operand.toExpression(shader), stride))
        );
    }
}
