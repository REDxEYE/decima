package com.red.dxbc.chunks.rdef;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Index;
import com.shade.platform.model.util.BufferUtils;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.EnumSet;

public class Variable {
    public final String name;
    public final VariableType type;
    public final byte[] defaultValue;
    public final int size;
    public final EnumSet<D3DShaderVariableFlags> flags;

    public Variable(@NotNull ByteBuffer buffer) {
        final int nameOffset = buffer.getInt();
        final int dataOffset = buffer.getInt();
        size = buffer.getInt();
        flags = IOUtils.getEnumSet(D3DShaderVariableFlags.class, buffer.getInt());
        final int varTypeNameOffset = buffer.getInt();
        final int defaultValueOffset = buffer.getInt();
        buffer.position(nameOffset);
        name = BufferUtils.getString(buffer);

        buffer.position(varTypeNameOffset);
        type = new VariableType(buffer);
        if (defaultValueOffset != 0) {
            buffer.position(defaultValueOffset);
            defaultValue = BufferUtils.getBytes(buffer, size);
        } else {
            defaultValue = null;
        }
    }
}
