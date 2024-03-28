package com.shade.decima.model.rtti.types.base;

import com.shade.decima.model.rtti.Type;
import com.shade.decima.model.rtti.types.java.HwVertexArray;
import com.shade.decima.model.rtti.types.java.RTTIField;

public abstract class BaseVertexArray implements HwVertexArray {
    @RTTIField(type = @Type(name = "uint32"))
    public int vertexCount;
    @RTTIField(type = @Type(name = "bool"), name = "IsStreaming")
    public boolean streaming;

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public boolean streaming() {
        return streaming;
    }

}
