package com.shade.decima.model.rtti.types.hzd;

import com.shade.decima.model.rtti.RTTIClass;
import com.shade.decima.model.rtti.Type;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.registry.RTTITypeRegistry;
import com.shade.decima.model.rtti.types.base.BaseVertexStream;
import com.shade.decima.model.rtti.types.base.BaseVertexStreamElement;
import com.shade.decima.model.rtti.types.java.HwDataSource;
import com.shade.decima.model.rtti.types.java.RTTIField;
import com.shade.platform.model.util.BufferUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class HZDVertexStream extends BaseVertexStream {
    @RTTIField(type = @Type(type = BaseVertexStreamElement[].class))
    public RTTIObject[] elements;
    @RTTIField(type = @Type(type = HZDDataSource.class))
    public RTTIObject dataSource;

    @NotNull
    public static RTTIObject read(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer, boolean streaming, int vertices) {
        final var flags = buffer.getInt();
        final var stride = buffer.getInt();
        final var elementsCount = buffer.getInt();
        final var elements = new RTTIObject[elementsCount];

        for (int j = 0; j < elementsCount; j++) {
            elements[j] = BaseVertexStreamElement.read(registry, buffer);
        }

        final var object = new HZDVertexStream();
        object.flags = flags;
        object.stride = stride;
        object.elements = elements;
        object.hash = registry.<RTTIClass>find("MurmurHashValue").read(registry, buffer);
        if (streaming) {
            object.dataSource = HZDDataSource.read(registry, buffer);
        } else {
            object.data = BufferUtils.getBytes(buffer, stride * vertices);
        }
        return new RTTIObject(registry.find(HZDVertexStream.class), object);
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

        if (dataSource != null) {
            dataSource.<HwDataSource>cast().write(registry, buffer);
        } else {
            buffer.put(data);
        }
    }

    @Override
    public int getSize() {
        int size = 28 + elements.length * BaseVertexStreamElement.getSize();

        if (dataSource != null) {
            size += dataSource.<HwDataSource>cast().getSize();
        } else {
            size += data.length;
        }

        return size;
    }
}
