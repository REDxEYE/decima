package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.SamplerMode;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareSampler extends Opcode {

    public final SamplerMode mode;
    public final OperandToken0 samplerRegister;
    public final int space;

    public DeclareSampler(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        mode = IOUtils.getEnum(SamplerMode.class, BitUnpacker.getIntRange(opcodeToken, 11, 14));
        samplerRegister = new OperandToken0(buffer);
        space = buffer.getInt();
    }

    @Override
    public String toString() {
        return "dcl_sampler %s, %s, space=%d".formatted(samplerRegister, mode.toString().toLowerCase(), space);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Sampler %s".formatted(samplerRegister.toExpression(shader)))
        );
    }
}
