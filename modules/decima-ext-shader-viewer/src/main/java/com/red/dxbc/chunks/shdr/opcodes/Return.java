package com.red.dxbc.chunks.shdr.opcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.util.NotNull;

import java.util.List;

public final class Return extends Opcode {

    public Return(int opcodeToken) {
        super(opcodeToken);
    }

    @Override
    public String toString() {
        return "ret";
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(new Commentary("return"));
    }

}
