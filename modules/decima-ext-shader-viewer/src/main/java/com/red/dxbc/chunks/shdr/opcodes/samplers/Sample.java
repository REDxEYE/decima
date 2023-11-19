package com.red.dxbc.chunks.shdr.opcodes.samplers;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.InstructionTokenExtendedType;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public class Sample extends Opcode {
    public final OperandToken0 output;
    public final OperandToken0 inputAddress;
    public final OperandToken0 inputResource;
    public final OperandToken0 inputSampler;
    public final byte[] sampleOffsets;


    public Sample(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        if (isExtended()) {
            final int extendedOpcodeToken = buffer.getInt();
            final InstructionTokenExtendedType extendedType = IOUtils.getEnum(InstructionTokenExtendedType.class, BitUnpacker.getInt(extendedOpcodeToken, 0, 6));
            if (extendedType != InstructionTokenExtendedType.SampleControls) {
                throw new IllegalStateException("Unexpected extension on sampler");
            }
            sampleOffsets = new byte[4];
            sampleOffsets[0] = (byte) IOUtils.signExtend(buffer.get(4), 4);
            sampleOffsets[1] = (byte) IOUtils.signExtend(buffer.get(4), 4);
            sampleOffsets[2] = (byte) IOUtils.signExtend(buffer.get(4), 4);
            sampleOffsets[3] = (byte) IOUtils.signExtend(buffer.get(4), 4);
        } else {
            sampleOffsets = null;
        }
        output = new OperandToken0(buffer);
        inputAddress = new OperandToken0(buffer);
        inputResource = new OperandToken0(buffer);
        inputSampler = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "sample %s, %s, %s, %s".formatted(output, inputAddress, inputResource, inputSampler);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        final Element[] arguments = {
            inputResource.toExpression(shader, 0),
            inputSampler.toExpression(shader, 0),
            inputAddress.toExpression(shader, -1),
        };

        if (sampleOffsets != null) {
            final String aoffimmi = "(%d, %d, %d, %d)".formatted(sampleOffsets[0], sampleOffsets[1], sampleOffsets[2], sampleOffsets[3]);
            return List.of(
                new Assignment(output.toExpression(shader),
                    new Expression("%s.Sample(%s, %s" + aoffimmi + ")", arguments)
                )
            );
        }
        return List.of(
            new Assignment(output.toExpression(shader),
                new Expression("%s.Sample(%s, %s)", arguments
                )
            )
        );
    }
}
