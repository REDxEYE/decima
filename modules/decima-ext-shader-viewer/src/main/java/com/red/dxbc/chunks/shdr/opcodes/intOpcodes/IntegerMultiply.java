package com.red.dxbc.chunks.shdr.opcodes.intOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.OperandType;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class IntegerMultiply extends Opcode {

    public final OperandToken0 highOutput;
    public final OperandToken0 lowOutput;
    public final OperandToken0 input0;
    public final OperandToken0 input1;

    public IntegerMultiply(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        if(isSaturated()){
            throw new IllegalStateException("Int math cannot be saturated");
        }
        highOutput = new OperandToken0(buffer, true);
        lowOutput = new OperandToken0(buffer, true);
        input0 = new OperandToken0(buffer, true);
        input1 = new OperandToken0(buffer, true);

    }

    @Override
    public String toString() {
        return "imul %s, %s, %s, %s".formatted(highOutput, lowOutput, input0, input1);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        final List<Element> elements = new ArrayList<>(1);
        elements.add(
            new Assignment(
                lowOutput.toExpression(shader),
                new Expression("(%s * %s) & 0xFFFFFFFF",
                    input0.toExpression(shader, lowOutput.componentSelection),
                    input1.toExpression(shader, lowOutput.componentSelection)
                )
            )
        );
        if (highOutput.operandType != OperandType.NULL) {
            elements.add(
                new Assignment(
                    highOutput.toExpression(shader),
                    new Expression("(%s * %s) <<32",
                        input0.toExpression(shader, lowOutput.componentSelection),
                        input1.toExpression(shader, lowOutput.componentSelection)
                    )
                )
            );
        }
        return elements;
    }
}
