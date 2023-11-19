package com.red.dxbc.chunks.shdr.opcodes.flowControl;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Statement;
import com.shade.util.NotNull;

import java.util.List;

public final class Else extends Opcode {


    public Else(int opcodeToken) {
        super(opcodeToken);

    }

    @Override
    public String toString() {
        return "else";
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Statement("}"),
            new Statement("else"),
            new Statement("{")
        );
    }
}
