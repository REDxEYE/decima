package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.ResourceReturnTypeToken;
import com.red.dxbc.chunks.shdr.enums.ResourceDimension;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareResource extends Opcode {

    public final ResourceDimension dimension;
    public final OperandToken0 operand;
    public final ResourceReturnTypeToken returnTypeToken;
    public final int space;

    public DeclareResource(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        dimension = IOUtils.getEnum(ResourceDimension.class, BitUnpacker.getIntRange(opcodeToken, 11, 15));
        operand = new OperandToken0(buffer);
        returnTypeToken = new ResourceReturnTypeToken(buffer.getInt());
        space = buffer.getInt();

    }

    @Override
    public String toString() {
        return "dcl_resource_%s %s %s%s[%s:%s], space=%d".formatted(dimension.toString().toLowerCase(), returnTypeToken, operand.operandData, operand.indices[0], operand.indices[1], operand.indices[2], space);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Resource %s".formatted(operand.toExpression(shader)))
        );
    }
}
