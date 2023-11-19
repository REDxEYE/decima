package com.red.dxbc.chunks.shdr.opcodes.intOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Assignment;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Function;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class UIntegerMinimum extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input0;
    public final OperandToken0 input1;


    public UIntegerMinimum(int opcodeToken, @NotNull ByteBuffer buffer) {

        super(opcodeToken);
        output = new OperandToken0(buffer);
        input0 = new OperandToken0(buffer, true);
        input1 = new OperandToken0(buffer, true);

    }

    @Override
    public String toString() {
        return "umin %s, %s, %s".formatted(output, input0, input1);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        Function function = new Function("min",
            input0.toExpression(shader, output.componentSelection),
            input1.toExpression(shader, output.componentSelection)
        );
        if (isSaturated()) {
            function = new Function("saturate", function);
        }
        return List.of(new Assignment(output.toExpression(shader), function));
    }

}
