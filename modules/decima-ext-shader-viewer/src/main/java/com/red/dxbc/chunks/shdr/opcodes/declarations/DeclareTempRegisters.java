package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareTempRegisters extends Opcode {

    public final int tempRegisters;

    public DeclareTempRegisters(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        tempRegisters = buffer.getInt();
    }

    @Override
    public String toString() {
        return "dcl_temps %d".formatted(tempRegisters);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Uses %d temp registers".formatted(tempRegisters))
        );
    }

}
