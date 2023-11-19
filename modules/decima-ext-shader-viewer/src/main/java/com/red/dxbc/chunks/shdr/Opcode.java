package com.red.dxbc.chunks.shdr;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.enums.ResinfoInstructionReturnType;
import com.red.dxbc.decompiler.Element;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.util.List;

public abstract class Opcode {
    protected final int opcodeToken;

    public Opcode(int opcodeToken) {
        this.opcodeToken = opcodeToken;
    }

    protected boolean isExtended() {
        return BitUnpacker.getBool(opcodeToken, 31);
    }

    protected boolean isSaturated() {
        return BitUnpacker.getBool(opcodeToken, 13);
    }

    protected TestBoolean getTestBoolean() {
        return IOUtils.getEnum(TestBoolean.class, BitUnpacker.getIntRange(opcodeToken, 18, 18));
    }

    protected ResinfoInstructionReturnType getResintoReturnType() {
        return IOUtils.getEnum(ResinfoInstructionReturnType.class, BitUnpacker.getIntRange(opcodeToken, 11, 12));
    }

    protected int getPreciseValueMask() {
        return BitUnpacker.getIntRange(opcodeToken, 19, 22);
    }

    public int getSize() {
        return BitUnpacker.getIntRange(opcodeToken, 24, 30);
    }

    public abstract List<Element> toExpressions(@NotNull DXBC shader);


}
