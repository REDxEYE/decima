package com.red.dxbc.chunks.shdr.opcodes.samplers;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class SampleLod extends Sample {
    public final OperandToken0 inputLod;


    public SampleLod(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);
        inputLod = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "sample_b %s, %s, %s, %s, %s".formatted(output, inputAddress, inputResource, inputSampler, inputLod);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        final Element[] arguments = {
            inputResource.toExpression(shader, 0),
            inputSampler.toExpression(shader, 0),
            inputAddress.toExpression(shader, -1),
            inputLod.toExpression(shader, 1)
        };

        if (sampleOffsets != null) {
            final String aoffimmi = "(%d, %d, %d, %d)".formatted(sampleOffsets[0], sampleOffsets[1], sampleOffsets[2], sampleOffsets[3]);
            return List.of(
                new Assignment(output.toExpression(shader),
                    new Expression("%s.SampleLod(%s, %s, %s" + aoffimmi + ")", arguments)
                )
            );
        }
        return List.of(
            new Assignment(output.toExpression(shader),
                new Expression("%s.SampleLod(%s, %s, %s)", arguments
                )
            )
        );
    }
}
