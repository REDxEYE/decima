package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.chunks.shdr.OperandToken0;
import com.red.dxbc.chunks.shdr.ResourceReturnTypeToken;
import com.red.dxbc.chunks.shdr.enums.ResourceDimension;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;

public final class DeclareUnorderedAccessViewTyped extends Opcode {

    public final ResourceDimension dimension;
    public final boolean globallyCoherentAccess;
    public final boolean rasterizerOrderedAccess;
    public final ResourceReturnTypeToken returnTypeToken;
    public final OperandToken0 operand;
    public final int space;

    // [10:00] D3D11_SB_OPCODE_DCL_UNORDERED_ACCESS_VIEW_TYPED
    // [15:11] D3D10_SB_RESOURCE_DIMENSION
    // [16:16] D3D11_SB_GLOBALLY_COHERENT_ACCESS or 0 (LOCALLY_COHERENT)
    // [17:17] D3D11_SB_RASTERIZER_ORDERED_ACCESS or 0
    // [23:18] Ignored, 0
    // [30:24] Instruction length in DWORDs including the opcode token.
    // [31]    0 normally. 1 if extended operand definition, meaning next DWORD
    //         contains extended operand description.  This dcl is currently not
    //         extended.
    public DeclareUnorderedAccessViewTyped(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        dimension = IOUtils.getEnum(ResourceDimension.class, BitUnpacker.getIntRange(opcodeToken, 11, 15));
        globallyCoherentAccess = BitUnpacker.getBool(opcodeToken, 16);
        rasterizerOrderedAccess = BitUnpacker.getBool(opcodeToken, 17);
        operand = new OperandToken0(buffer);
        returnTypeToken = new ResourceReturnTypeToken(buffer.getInt());
        space = buffer.getInt();
    }

    @Override
    public String toString() {
        return "dcl_uav_typed %s%s[%s:%s], %s, space=%d".formatted(operand.operandData, operand.indices[0], operand.indices[1], operand.indices[2], returnTypeToken, space);
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Unordered Access view typed %s: %s".formatted(operand.toExpression(shader), returnTypeToken))
        );
    }
}
