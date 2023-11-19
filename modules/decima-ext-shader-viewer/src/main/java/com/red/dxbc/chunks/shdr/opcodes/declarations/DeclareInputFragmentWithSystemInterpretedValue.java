package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.enums.NameToken;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareInputFragmentWithSystemInterpretedValue extends DeclareInputFragment {
    public final NameToken systemInterpretedValue;

    public DeclareInputFragmentWithSystemInterpretedValue(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken, buffer);
        systemInterpretedValue = IOUtils.getEnum(NameToken.class, buffer.getInt());
    }


    @Override
    public String toString() {
        return "dcl_input_ps_siv %s %s, %s".formatted(interpolationMode.name().toLowerCase(), operand, systemInterpretedValue);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Input %s%s fragment: %s".formatted(operand.toExpression(shader, 0), operand.indices[0], systemInterpretedValue))
        );
    }

}
