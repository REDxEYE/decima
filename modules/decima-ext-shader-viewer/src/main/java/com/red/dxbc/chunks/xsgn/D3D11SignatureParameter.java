package com.red.dxbc.chunks.xsgn;

import com.red.dxbc.ComponentMask;
import com.shade.platform.model.util.BufferUtils;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class D3D11SignatureParameter {
    public final String semanticName;
    public final int semanticIndex;
    public final int register;
    public final ComponentMask mask;
    public final ComponentMask readWriteMask;
    public final int stream;
    public final D3DName systemValueType;
    public final D3DRegisterComponentType componentType;
    public final D3DMinPrecision minPrecision;

    public D3D11SignatureParameter(@NotNull ByteBuffer buffer, int elementSize) {
        minPrecision = D3DMinPrecision.MIN_PRECISION_DEFAULT;
        if (elementSize == 7)
            stream = buffer.getInt();
        else
            stream = 0;
        final int nameOffset = buffer.getInt();
        semanticIndex = buffer.getInt();
        systemValueType = IOUtils.getEnum(D3DName.class, buffer.getInt());
        componentType = IOUtils.getEnum(D3DRegisterComponentType.class, buffer.getInt());
        register = buffer.getInt();
        final int mask = buffer.getInt();
        readWriteMask = IOUtils.getEnum(ComponentMask.class, (mask >> 8) & 0xFF);
        this.mask = IOUtils.getEnum(ComponentMask.class, mask & 0xFF);


        final int tmp = buffer.position();
        buffer.position(nameOffset);
        semanticName = BufferUtils.getString(buffer);
        buffer.position(tmp);
    }

    public String formattedRepr() {
        return semanticName + '_' + semanticIndex;
    }
}
