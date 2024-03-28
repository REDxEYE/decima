package com.shade.decima.model.rtti.types.ds;

import com.shade.decima.model.rtti.Type;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.registry.RTTITypeRegistry;
import com.shade.decima.model.rtti.types.base.BaseVertexArray;
import com.shade.decima.model.rtti.types.java.HwVertexStream;
import com.shade.decima.model.rtti.types.java.RTTIField;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DSVertexArray extends BaseVertexArray {

    @RTTIField(type = @Type(type = DSVertexStream[].class))
    public RTTIObject[] streams;

    @NotNull
    public static RTTIObject read(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer) {
        final var vertexCount = buffer.getInt();
        final var streamCount = buffer.getInt();
        final var streaming = buffer.get() != 0;
        final var streams = new RTTIObject[streamCount];

        for (int i = 0; i < streamCount; i++) {
            streams[i] = DSVertexStream.read(registry, buffer, streaming, vertexCount);
        }

        final var object = new DSVertexArray();
        object.vertexCount = vertexCount;
        object.streaming = streaming;
        object.streams = streams;

        return new RTTIObject(registry.find(DSVertexArray.class), object);
    }

    public void write(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer) {
        buffer.putInt(vertexCount);
        buffer.putInt(streams.length);
        buffer.put((byte) (streaming ? 1 : 0));

        for (RTTIObject stream : streams) {
            stream.<DSVertexStream>cast().write(registry, buffer);
        }
    }

    public int getSize() {
        return 9 + Arrays.stream(streams)
            .map(RTTIObject::<DSVertexStream>cast)
            .mapToInt(DSVertexStream::getSize)
            .sum();
    }

    @NotNull
    @Override
    public HwVertexStream[] streams() {
        return Arrays.stream(streams)
            .map(RTTIObject::<DSVertexStream>cast)
            .toArray(HwVertexStream[]::new);
    }
}
