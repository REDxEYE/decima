package com.red.dxbc.chunks;

import com.red.dxbc.Chunk;

public class DummyChunk extends Chunk {
    public final byte[] data;

    public DummyChunk(String name, byte[] data) {
        super(name);
        this.data = data;
    }
}
