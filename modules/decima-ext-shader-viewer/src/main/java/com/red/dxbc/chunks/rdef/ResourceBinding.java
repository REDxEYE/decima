package com.red.dxbc.chunks.rdef;

import com.shade.platform.model.util.BufferUtils;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class ResourceBinding {
    public final String name;
    public final D3DShaderInputType shaderInputType;
    public final int bindPoint;
    public final int bindCount;
    public final int flags;
    public final D3DResourceReturnType returnType;
    public final D3DSRVDimension dimension;
    public final int sampleCount;
    public final int space;
    public final int uID;

    public ResourceBinding(@NotNull ByteBuffer buffer, int stringOffset) {
        final int nameOffset = buffer.getInt();
        shaderInputType = IOUtils.getEnum(D3DShaderInputType.class, buffer.getInt());
        returnType = IOUtils.getEnum(D3DResourceReturnType.class, buffer.getInt());
        dimension = IOUtils.getEnum(D3DSRVDimension.class, buffer.getInt());
        sampleCount = buffer.getInt();
        bindPoint = buffer.getInt();
        bindCount = buffer.getInt();
        flags = buffer.getInt();
        space = buffer.getInt();
        uID = buffer.getInt();
        final int tmp = buffer.position();
        buffer.position(nameOffset);
        name = BufferUtils.getString(buffer);
        buffer.position(tmp);
    }
}
