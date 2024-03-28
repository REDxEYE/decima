package com.shade.decima.model.rtti.types.java;

import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.util.NotNull;

public interface HwVertexStream extends HwType {
    @NotNull
    RTTIObject[] elements();

    @NotNull
    byte[] data();

    int flags();

    int stride();
}
