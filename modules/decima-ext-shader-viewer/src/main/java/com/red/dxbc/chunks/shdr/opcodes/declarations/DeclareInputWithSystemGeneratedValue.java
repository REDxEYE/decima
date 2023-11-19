package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.enums.NameToken;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareInputWithSystemGeneratedValue extends DeclareInput {
    public final NameToken systemGeneratedValue;

    public DeclareInputWithSystemGeneratedValue(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);
        systemGeneratedValue = IOUtils.getEnum(NameToken.class, buffer.getInt());

    }


    @Override
    public String toString() {
        return "dcl_input_sgv %s, %s".formatted(operand, systemGeneratedValue.name());
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Input %s%s : %s".formatted(operand.toExpression(shader, 0), operand.indices[0], systemGeneratedValue))
        );
    }

}
