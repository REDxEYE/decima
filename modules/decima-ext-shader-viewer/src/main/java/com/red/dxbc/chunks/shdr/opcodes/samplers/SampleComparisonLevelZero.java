package com.red.dxbc.chunks.shdr.opcodes.samplers;

import com.red.dxbc.DXBC;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class SampleComparisonLevelZero extends SampleComparison {

    public SampleComparisonLevelZero(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);
    }

    @Override
    public String toString() {
        if (sampleOffsets != null)
            return "sample_c_lz_aoffimmi(%d, %d, %d, %d) %s, %s, %s, %s, %s".formatted(sampleOffsets[0], sampleOffsets[1], sampleOffsets[2], sampleOffsets[3], output, inputAddress, inputResource, inputSampler, inputReferenceValue);
        return "sample_c_lz %s, %s, %s, %s, %s".formatted(output, inputAddress, inputResource, inputSampler, inputReferenceValue);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        final Element[] arguments = {
            inputResource.toExpression(shader, 0),
            inputSampler.toExpression(shader, 0),
            inputAddress.toExpression(shader, -1),
            inputReferenceValue.toExpression(shader, 1)
        };

        if (sampleOffsets != null) {
            final String aoffimmi = "(%d, %d, %d, %d)".formatted(sampleOffsets[0], sampleOffsets[1], sampleOffsets[2], sampleOffsets[3]);
            return List.of(
                new Assignment(output.toExpression(shader),
                    new Expression("%s.SampleWithCompareLZ(%s, %s, %s" + aoffimmi + ")", arguments)
                )
            );
        }
        return List.of(
            new Assignment(output.toExpression(shader),
                new Expression("%s.SampleWithCompareLZ(%s, %s, %s)", arguments
                )
            )
        );
    }
}
