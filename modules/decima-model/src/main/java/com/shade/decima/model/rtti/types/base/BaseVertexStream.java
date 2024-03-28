package com.shade.decima.model.rtti.types.base;

import com.shade.decima.model.rtti.Type;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.types.java.HwVertexStream;
import com.shade.decima.model.rtti.types.java.RTTIField;
import com.shade.util.NotNull;

public abstract class BaseVertexStream implements HwVertexStream {
    @RTTIField(type = @Type(name = "MurmurHashValue"))
    public RTTIObject hash;
    @RTTIField(type = @Type(name = "Array<uint8>"))
    public byte[] data;
    @RTTIField(type = @Type(name = "uint32"))
    public int flags;
    @RTTIField(type = @Type(name = "uint32"))
    public int stride;

    @NotNull
    @Override
    public byte[] data() {
        return data;
    }

    @Override
    public int flags() {
        return flags;
    }

    @Override
    public int stride() {
        return stride;
    }
}
