package com.shade.decima.model.rtti.types.base;

import com.shade.decima.model.rtti.Type;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.registry.RTTITypeRegistry;
import com.shade.decima.model.rtti.types.RTTITypeEnum;
import com.shade.decima.model.rtti.types.java.HwVertexStreamElement;
import com.shade.decima.model.rtti.types.java.RTTIField;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class BaseVertexStreamElement implements HwVertexStreamElement {
    @RTTIField(type = @Type(name = "EVertexElementStorageType"))
    public RTTITypeEnum.Constant storageType;
    @RTTIField(type = @Type(name = "EVertexElement"))
    public RTTITypeEnum.Constant type;
    @RTTIField(type = @Type(name = "uint8"))
    public byte usedSlots;
    @RTTIField(type = @Type(name = "uint8"))
    public byte offset;

    @NotNull
    public static RTTIObject read(@NotNull RTTITypeRegistry registry, @NotNull ByteBuffer buffer) {
        final var object = new BaseVertexStreamElement();
        object.offset = buffer.get();
        object.storageType = registry.<RTTITypeEnum>find("EVertexElementStorageType").valueOf(buffer.get());
        object.usedSlots = buffer.get();
        object.type = registry.<RTTITypeEnum>find("EVertexElement").valueOf(buffer.get());

        return new RTTIObject(registry.find(BaseVertexStreamElement.class), object);
    }

    public void write(@NotNull ByteBuffer buffer) {
        buffer.put(offset);
        buffer.put((byte) storageType.value());
        buffer.put(usedSlots);
        buffer.put((byte) type.value());
    }

    public static int getSize() {
        return 4;
    }

    @NotNull
    @Override
    public RTTITypeEnum.Constant storageType() {
        return storageType;
    }

    @NotNull
    @Override
    public RTTITypeEnum.Constant type() {
        return type;
    }

    @Override
    public byte usedSlots() {
        return usedSlots;
    }

    @Override
    public byte offset() {
        return offset;
    }
}
