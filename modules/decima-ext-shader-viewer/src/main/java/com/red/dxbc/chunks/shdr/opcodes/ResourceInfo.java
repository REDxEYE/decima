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

public final class ResourceInfo extends Opcode {

    public final OperandToken0 output;
    public final OperandToken0 inputMipLevel;
    public final OperandToken0 inputResource;

    public ResourceInfo(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        output = new OperandToken0(buffer);
        inputMipLevel = new OperandToken0(buffer);
        inputResource = new OperandToken0(buffer);

    }

    @Override
    public String toString() {
        String resType = switch (getResintoReturnType()) {

            case D3D10_SB_RESINFO_INSTRUCTION_RETURN_FLOAT -> "";
            case D3D10_SB_RESINFO_INSTRUCTION_RETURN_RCPFLOAT -> "_rcpFloat";
            case D3D10_SB_RESINFO_INSTRUCTION_RETURN_UINT -> "_uint";
        };

        return "resinfo%s %s, %s, %s".formatted(resType, output, inputMipLevel, inputResource);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Assignment(output.toExpression(shader),
                new Function("resInfo",
                    inputResource.toExpression(shader, 0),
                    inputMipLevel.toExpression(shader, 1)
                )
            )
        );
    }

}
