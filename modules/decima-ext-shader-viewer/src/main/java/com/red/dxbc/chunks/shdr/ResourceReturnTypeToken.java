package com.red.dxbc.chunks.shdr;

import com.red.dxbc.chunks.shdr.enums.ResourceReturnType;
import com.shade.platform.model.util.BitUnpacker;
import com.shade.platform.model.util.IOUtils;

public class ResourceReturnTypeToken {
    public final ResourceReturnType x;
    public final ResourceReturnType y;
    public final ResourceReturnType z;
    public final ResourceReturnType w;

    public ResourceReturnTypeToken(int value) {
        x = IOUtils.getEnum(ResourceReturnType.class, BitUnpacker.getInt(value, 0, 4));
        y = IOUtils.getEnum(ResourceReturnType.class, BitUnpacker.getInt(value, 4, 4));
        z = IOUtils.getEnum(ResourceReturnType.class, BitUnpacker.getInt(value, 8, 4));
        w = IOUtils.getEnum(ResourceReturnType.class, BitUnpacker.getInt(value, 12, 4));
    }

    @Override
    public String toString() {
        return "(%s, %s, %s, %s)".formatted(x.toString().toLowerCase(), y.toString().toLowerCase(), z.toString().toLowerCase(), w.toString().toLowerCase());
    }
}
