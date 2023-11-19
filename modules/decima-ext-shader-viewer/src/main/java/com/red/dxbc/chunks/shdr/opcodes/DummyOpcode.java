package com.red.dxbc.chunks.shdr.opcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DummyOpcode extends Opcode {

    public final int[] data;

    public DummyOpcode(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        final int size = BitUnpacker.getIntRange(opcodeToken, 24, 30);
        if (size > 1) {
            data = new int[size - 1];
            for (int i = 0; i < size - 1; i++) {
                data[i] = buffer.getInt();
            }
        } else {
            data = null;
        }
    }

    @Override
    public String toString() {
        return "DummyOpcode{" + "extended=" + isExtended() + ", size=" + data.length + '}';
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("FIX ME!!!!")
        );
    }

}
