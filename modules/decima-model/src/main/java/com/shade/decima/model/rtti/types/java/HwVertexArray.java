package com.shade.decima.model.rtti.types.java;

import com.shade.util.NotNull;

public interface HwVertexArray {
    int vertexCount();

    boolean streaming();

    @NotNull
    HwVertexStream[] streams();
}
