package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.ConstantBufferAccessPattern;
import com.red.dxbc.chunks.shdr.enums.OperandType;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareConstantBuffer extends Opcode {
    public final ConstantBufferAccessPattern constantAccess;

    public final OperandToken0 firstOperand;
    public final int bufferSize;
    public final int space;

    public DeclareConstantBuffer(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        constantAccess = IOUtils.getEnum(ConstantBufferAccessPattern.class, BitUnpacker.getIntRange(opcodeToken, 11, 11));
        firstOperand = new OperandToken0(buffer);
        if (firstOperand.operandType != OperandType.CONSTANT_BUFFER) {
            throw new IllegalArgumentException("Expected CONSTANT_BUFFER operand type, got %s".formatted(firstOperand.operandType.name()));
        }
        bufferSize = buffer.getInt();
        space = buffer.getInt();

    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("dcl_constantbuffer %s%s[%s:%s][%d], ".formatted(firstOperand.operandData, firstOperand.indices[0], firstOperand.indices[1], firstOperand.indices[2], bufferSize));
        if (constantAccess == ConstantBufferAccessPattern.IMMEDIATE_INDEXED) sb.append("immediateIndexed, ");
        else sb.append("dynamicIndexed, ");
        sb.append("space=%d".formatted(space));

        return sb.toString();
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Constant buffer CB%s".formatted(firstOperand.indices[0]))
        );
    }

}
