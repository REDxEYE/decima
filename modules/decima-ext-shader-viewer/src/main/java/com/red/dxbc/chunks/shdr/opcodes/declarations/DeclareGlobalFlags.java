package com.red.dxbc.chunks.shdr.opcodes.declarations;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Opcode;
import com.red.dxbc.decompiler.Commentary;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.EnumSetValue;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

public final class DeclareGlobalFlags extends Opcode {
    public final Set<GlobalFlags> flags;


    public DeclareGlobalFlags(int opcodeToken, @NotNull ByteBuffer buffer) {
        super(opcodeToken);
        flags = IOUtils.getEnumSet(GlobalFlags.class, BitUnpacker.getIntRange(opcodeToken, 11, 18));
    }


    @Override
    public String toString() {
        return "dcl_globalFlags" + flags.toString();
    }

    @Override
    public List<Element> toExpressions(@NotNull DXBC shader) {
        return List.of(
            new Commentary("Global flags %s".formatted(flags.toString()))
        );
    }

    public enum GlobalFlags implements EnumSetValue {
        RefactoringAllowed(1),
        EnableDoublePrecisionFloatOps(2),
        ForceEarlyDepthStencilTest(4),
        EnableRawAndStructuredBuffersInNonCsShaders(8),
        SkipOptimizationsOfShaderIl(16),
        EnableMinimumPrecisionDataTypes(32),
        Enable11_1DoublePrecisionInstructionExtensions(64),
        Enable11_1NonDoubleInstructionExtensions(128);

        private final int value;

        GlobalFlags(int value) {
            this.value = value;
        }


        @Override
        public int value() {
            return value;
        }
    }
}
