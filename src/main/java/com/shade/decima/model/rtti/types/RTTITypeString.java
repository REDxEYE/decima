package com.shade.decima.model.rtti.types;

import com.shade.decima.model.rtti.RTTIDefinition;
import com.shade.decima.model.rtti.RTTIType;
import com.shade.decima.model.rtti.registry.RTTITypeRegistry;
import com.shade.decima.model.util.hash.CRC32C;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RTTIDefinition("String")
public class RTTITypeString extends RTTIType<String> {
    private final String name;

    public RTTITypeString(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String read(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer) {
        final int size = buffer.getInt();
        if (size > 0) {
            final int hash = buffer.getInt();
            final byte[] data = IOUtils.getBytesExact(buffer, size);
            if (hash != CRC32C.calculate(data)) {
                throw new IllegalArgumentException("Data is corrupted (mismatched checksum)");
            }
            return new String(data, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    @Override
    public void write(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer, @NotNull String value) {
        final byte[] data = value.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(data.length);
        if (data.length > 0) {
            buffer.putInt(CRC32C.calculate(data));
            buffer.put(data);
        }
    }

    @Override
    public int getSize(@NotNull RTTITypeRegistry registry, @NotNull String value) {
        if (value.isEmpty()) {
            return Integer.BYTES;
        } else {
            return Integer.BYTES * 2 + value.getBytes(StandardCharsets.UTF_8).length;
        }
    }

    @NotNull
    @Override
    public String getTypeName() {
        return name;
    }

    @NotNull
    @Override
    public Class<String> getInstanceType() {
        return String.class;
    }
}
