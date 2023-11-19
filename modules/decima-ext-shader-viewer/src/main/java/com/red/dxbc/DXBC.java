package com.red.dxbc;

import com.red.dxbc.chunks.DummyChunk;
import com.red.dxbc.chunks.rdef.ResourceDefinitions;
import com.red.dxbc.chunks.shdr.ShaderCode;
import com.red.dxbc.chunks.xsgn.InputSignature;
import com.red.dxbc.chunks.xsgn.OutputSignature;
import com.shade.platform.model.util.BufferUtils;
import com.shade.util.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DXBC {
    private static final int DXBC_MAGIC = 'D' << 24 | 'X' << 16 | 'B' << 8 | 'C';

    private final Map<String, Chunk> chunks = new HashMap<>();
    private final byte[] checksum;


    public DXBC(byte[] shaderBytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(shaderBytes).order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.getInt() == DXBC_MAGIC) {
            throw new IllegalArgumentException("Not a DirectX ByteCode");
        }
        checksum = BufferUtils.getBytes(buffer, 16);
        if (buffer.getInt() != 1) {
            throw new IllegalArgumentException("Unsupported DXBC version");
        }
        if (buffer.getInt() != shaderBytes.length) {
            throw new IllegalArgumentException("Shader bytes size does not match expected size");
        }
        final int chunkCount = buffer.getInt();
        final List<Integer> chunkOffsets = new ArrayList<>(chunkCount);
        for (int i = 0; i < chunkCount; i++) {
            chunkOffsets.add(buffer.getInt());
        }
        for (Integer chunkOffset : chunkOffsets) {
            buffer.position(chunkOffset);
            String chunkName = BufferUtils.getString(buffer, 4);
            int chunkSize = buffer.getInt();
            final byte[] chunkData = BufferUtils.getBytes(buffer, chunkSize);
            Chunk chunk = switch (chunkName) {
                case "RDEF" -> new ResourceDefinitions(chunkName, chunkData);
                case "ISGN" -> new InputSignature(chunkName, chunkData);
                case "OSGN" -> new OutputSignature(chunkName, chunkData);
                case "SHEX", "SHDR" -> new ShaderCode(chunkName, chunkData);
                default -> new DummyChunk(chunkName, chunkData);
            };
            chunks.put(chunkName, chunk);

        }
    }

    public @Nullable Chunk getChunk(String name) {
        return chunks.getOrDefault(name, null);
    }
}
