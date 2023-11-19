package com.red.dxbc.chunks.shdr.opcodes.samplers;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public class Gather4WithOffsetComparison extends Opcode {
    public final OperandToken0 output;
    public final OperandToken0 inputAddress;
    public final OperandToken0 inputOffset;
    public final OperandToken0 inputResource;
    public final OperandToken0 inputSampler;
    public final OperandToken0 referenceValue;


    public Gather4WithOffsetComparison(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        inputAddress = new OperandToken0(buffer);
        inputOffset = new OperandToken0(buffer);
        inputResource = new OperandToken0(buffer);
        inputSampler = new OperandToken0(buffer);
        referenceValue = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "gather4_po %s, %s, %s, %s, %s, %s".formatted(output, inputAddress, inputOffset, inputResource, inputSampler, referenceValue);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        final Element[] arguments = {
            inputResource.toExpression(shader, 0),
            inputSampler.toExpression(shader, 0),
            inputAddress.toExpression(shader, -1),
            inputOffset.toExpression(shader, -1),
            referenceValue.toExpression(shader, -1),
        };

        return List.of(
            new Assignment(output.toExpression(shader),
                new Expression("%s.GatherWithOffsetComparison(%s, %s, %s, %s)", arguments
                )
            )
        );
    }
}
