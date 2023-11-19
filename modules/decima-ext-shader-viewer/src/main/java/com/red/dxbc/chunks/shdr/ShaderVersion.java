package com.red.dxbc.chunks.shdr;

import com.red.dxbc.chunks.shdr.enums.ProgramType;
import com.shade.platform.model.util.IOUtils;

import java.nio.ByteBuffer;

public class ShaderVersion {
    public final byte majorVersion;
    public final byte minorVersion;
    public final ProgramType programType;

    public ShaderVersion(ByteBuffer buffer) {
        short version = buffer.getShort();
        minorVersion = (byte) (version & 15);
        majorVersion = (byte) ((version & 240) >> 4);
        programType = IOUtils.getEnum(ProgramType.class, buffer.getShort());
    }

}
