package com.red.dxbc.chunks.shdr.opcodes.flowControl;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.red.dxbc.decompiler.Statement;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class BreakConditional extends Opcode {

    public final OperandToken0 input;

    public BreakConditional(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        input = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return switch (getTestBoolean()) {
            case TEST_ZERO -> "breakc_z %s".formatted(input);
            case TEST_NONZERO -> "breakc_nz %s".formatted(input);
        };
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            switch (getTestBoolean()) {

                case TEST_ZERO -> new Expression("if (%s == 0)", input.toExpression(shader, -1));
                case TEST_NONZERO -> new Expression("if (%s != 0)", input.toExpression(shader, -1));
            },
            new Statement("{"),
            new Statement("break"),
            new Statement("}")
        );
    }
}
