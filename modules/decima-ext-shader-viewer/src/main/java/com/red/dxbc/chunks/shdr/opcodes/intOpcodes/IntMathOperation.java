package com.red.dxbc.chunks.shdr.opcodes.intOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

abstract class IntMathOperation extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input0;
    public final OperandToken0 input1;


    public IntMathOperation(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        if(isSaturated()){
            throw new IllegalStateException("Int math cannot be saturated");
        }
        output = new OperandToken0(buffer);
        input0 = new OperandToken0(buffer, true);
        input1 = new OperandToken0(buffer, true);
    }

    protected abstract Expression innerExpression(@NotNull DXBC shader);

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        Element element = innerExpression(shader);
        return List.of(new Assignment(output.toExpression(shader), element));
    }
}
