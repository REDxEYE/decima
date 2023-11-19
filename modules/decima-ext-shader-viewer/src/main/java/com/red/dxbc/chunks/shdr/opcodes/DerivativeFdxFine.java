package com.red.dxbc.chunks.shdr.opcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Function;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DerivativeFdxFine extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input;

    public DerivativeFdxFine(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        input = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "DERIV_RTX_FINE %s, %s".formatted(output, input);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Assignment(output.toExpression(shader),
                new Function("dFdxFine ",
                    input.toExpression(shader, -1)
                )
            )
        );
    }

}
