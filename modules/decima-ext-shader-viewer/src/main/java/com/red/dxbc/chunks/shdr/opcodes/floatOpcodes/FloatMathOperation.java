package com.red.dxbc.chunks.shdr.opcodes.floatOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.red.dxbc.decompiler.Function;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

abstract class FloatMathOperation extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input0;
    public final OperandToken0 input1;


    public FloatMathOperation(int opcodeToken, @NotNull ByteBuffer buffer) {

        super(opcodeToken);
        output = new OperandToken0(buffer);
        input0 = new OperandToken0(buffer);
        input1 = new OperandToken0(buffer);
    }

    protected abstract Expression innerExpression(@NotNull DXBC shader);

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        Element element = innerExpression(shader);
        if (isSaturated())
            element = new Function("saturate", element);
        return List.of(new Assignment(output.toExpression(shader), element));
    }
}
