package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.enums.InterpolationMode;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public class DeclareInputFragment extends Opcode {

    public final OperandToken0 operand;
    public final InterpolationMode interpolationMode;

    public DeclareInputFragment(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        interpolationMode = IOUtils.getEnum(InterpolationMode.class, BitUnpacker.getIntRange(opcodeToken, 11, 14));
        operand = new OperandToken0(buffer);
    }


    @Override
    public String toString() {
        return "dcl_input_ps %s %s".formatted(interpolationMode.name().toLowerCase(), operand);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Input %s%s fragment".formatted(operand.toExpression(shader, 0), operand.indices[0]))
        );
    }

}
