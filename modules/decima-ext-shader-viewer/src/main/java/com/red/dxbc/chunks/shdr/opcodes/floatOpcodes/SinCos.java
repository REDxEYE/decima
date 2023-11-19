package com.red.dxbc.chunks.shdr.opcodes.floatOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.OperandType;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Function;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class SinCos extends Opcode {
    public final OperandToken0 output0;
    public final OperandToken0 output1;
    public final OperandToken0 input;

    public SinCos(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output0 = new OperandToken0(buffer);
        output1 = new OperandToken0(buffer);
        input = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "sincos %s, %s, %s".formatted(output0, output1, input);
    }

    public List<Element> toExpressions(@NotNull DXBC shader) {
        List<Element> exprs = new ArrayList<>(1);
        if (output0.operandType != OperandType.NULL) {
            exprs.add(new Assignment(output0.toExpression(shader, output0.componentSelection), new Function("sin", input.toExpression(shader, -1))));
        }
        if (output1.operandType != OperandType.NULL) {
            exprs.add(new Assignment(output1.toExpression(shader, output1.componentSelection), new Function("cos", input.toExpression(shader, -1))));
        }
        return exprs;
    }

}
