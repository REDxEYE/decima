package com.red.dxbc.chunks.shdr.opcodes.samplers;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.InstructionTokenExtendedType;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class FetchStructured extends Opcode {
    public final OperandToken0 output;
    public final OperandToken0 inputAddress;
    public final OperandToken0 inputBitOffset;
    public final OperandToken0 inputResource;

    public FetchStructured(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        inputAddress = new OperandToken0(buffer);
        inputBitOffset = new OperandToken0(buffer);
        inputResource = new OperandToken0(buffer);
    }

    @Override
    public String toString() {
        return "ld_structured %s, %s, %s".formatted(output, inputAddress, inputResource);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        final Element[] arguments = {
            inputResource.toExpression(shader, output.componentSelection),
            inputBitOffset.toExpression(shader, 1),
            inputAddress.toExpression(shader, -1),
        };

        return List.of(
            new Assignment(output.toExpression(shader),
                new Expression("%s.FetchStructured(%s, %s)", arguments
                )
            )
        );
    }
}
