package com.red.dxbc.chunks.rdef;

import com.red.dxbc.Chunk;
import com.shade.platform.model.util.BufferUtils;
import com.shade.platform.model.util.IOUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ResourceDefinitions extends Chunk {
    public final byte majorVersion;
    public final byte minorVersion;
    public final ProgramType programType;
    public final EnumSet<ShaderFlags> shaderFlags;
    public final String creator;

    public final List<ConstantBuffer> constantBuffers;
    public final List<ResourceBinding> resourceBindings;

    public ResourceDefinitions(String name, byte[] data) {
        super(name);
        final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        final int constantBufferCount = buffer.getInt();
        final int constantBufferOffset = buffer.getInt();
        final int resourceBindingCount = buffer.getInt();
        final int resourceBindingOffset = buffer.getInt();
        majorVersion = buffer.get();
        minorVersion = buffer.get();
        programType = IOUtils.getEnum(ProgramType.class, buffer.getShort() & 0xFFFF);
        shaderFlags = IOUtils.getEnumSet(ShaderFlags.class, buffer.getInt());
        final int creatorOffset = buffer.getInt();
        buffer.position(creatorOffset);
        creator = BufferUtils.getString(buffer);
        constantBuffers = new ArrayList<>(constantBufferCount);
        for (int i = 0; i < constantBufferCount; i++) {
            buffer.position(constantBufferOffset + 24 * i);
            constantBuffers.add(new ConstantBuffer(buffer));
        }
        buffer.position(resourceBindingOffset);
        resourceBindings = new ArrayList<>(resourceBindingCount);
        for (int i = 0; i < resourceBindingCount; i++) {
            resourceBindings.add(new ResourceBinding(buffer, resourceBindingOffset + 8 * 4 * resourceBindingCount));
        }
    }

    public ResourceBinding getResourceByBindPoint(int id) {
        for (ResourceBinding resourceBinding : resourceBindings) {
            if (resourceBinding.uID == id) {
                return resourceBinding;
            }
        }
        return null;
    }
}
