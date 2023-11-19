package com.red.dxbc.chunks.shdr.opcodes.floatOpcodes;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.decompiler.Element;
import com.red.dxbc.decompiler.Expression;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class Float16ToFloat32 extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 input;

    public Float16ToFloat32(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        input = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        return "f16tof32 %s, %s".formatted(output, input);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader){
        return List.of(new Expression("float()",
            input.toExpression(shader, output.componentSelection)
        ));
    }
}
