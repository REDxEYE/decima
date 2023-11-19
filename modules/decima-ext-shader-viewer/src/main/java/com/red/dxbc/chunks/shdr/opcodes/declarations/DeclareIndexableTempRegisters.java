package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.VariableArrayDeclaration;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareIndexableTempRegisters extends Opcode {

    public final int index;
    public final int length;
    public final int componentCount;

    public DeclareIndexableTempRegisters(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        index = buffer.getInt();
        length = buffer.getInt();
        componentCount = buffer.getInt();
    }

    @Override
    public String toString() {
        return "dcl_indexableTemp x%d[%d], %d".formatted(index, length, componentCount);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new VariableArrayDeclaration("float%d".formatted(componentCount), "x%d".formatted(index), componentCount)
        );
    }
}
