package com.shade.decima.model.rtti.types.java;

import com.shade.decima.model.rtti.types.RTTITypeEnum;
import com.shade.util.NotNull;

public interface HwVertexStreamElement {
    @NotNull
    RTTITypeEnum.Constant storageType();

    @NotNull
    RTTITypeEnum.Constant type();

    byte usedSlots();

    byte offset();
}
