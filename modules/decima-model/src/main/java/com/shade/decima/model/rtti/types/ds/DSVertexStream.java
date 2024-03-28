package com.shade.decima.model.rtti.types.ds;

import com.shade.decima.model.rtti.RTTIClass;
import com.shade.decima.model.rtti.Type;
import com.shade.decima.model.rtti.messages.ds.DSVertexArrayResourceHandler;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.registry.RTTITypeRegistry;
import com.shade.decima.model.rtti.types.base.BaseVertexStream;
import com.shade.decima.model.rtti.types.base.BaseVertexStreamElement;
import com.shade.decima.model.rtti.types.java.RTTIField;
import com.shade.platform.model.util.BufferUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class DSVertexStream extends BaseVertexStream {
    @RTTIField(type = @Type(type = BaseVertexStreamElement[].class))
    public RTTIObject[] elements;

    @NotNull
    public static RTTIObject read(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer, boolean streaming, int vertices) {
        final var flags = buffer.getInt();
        final var stride = buffer.getInt();
        final var elementsCount = buffer.getInt();
        final var elements = new RTTIObject[elementsCount];

        for (int j = 0; j < elementsCount; j++) {
            elements[j] = BaseVertexStreamElement.read(registry, buffer);
        }

        final var object = new DSVertexStream();
        object.flags = flags;
        object.stride = stride;
        object.elements = elements;
        object.hash = registry.<RTTIClass>find("MurmurHashValue").read(registry, buffer);
        object.data = streaming ? new byte[0] : BufferUtils.getBytes(buffer, stride * vertices);

        return new RTTIObject(registry.find(DSVertexStream.class), object);
    }

    @NotNull
    @Override
    public RTTIObject[] elements() {
        return elements;
    }

    @Override
    public void write(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer) {
        buffer.putInt(flags);
        buffer.putInt(stride);
        buffer.putInt(elements.length);

        for (RTTIObject element : elements) {
            element.<BaseVertexStreamElement>cast().write(buffer);
        }

        hash.type().write(registry, buffer, hash);
        buffer.put(data);
    }

    @Override
    public int getSize() {
        return 28 + data.length + elements.length * BaseVertexStreamElement.getSize();
    }
}
