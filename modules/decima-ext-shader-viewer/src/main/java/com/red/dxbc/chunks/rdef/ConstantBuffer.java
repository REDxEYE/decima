package com.red.dxbc.chunks.rdef;

import com.red.dxbc.chunks.shdr.Index;
import com.shade.platform.model.util.BufferUtils;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.*;

public class ConstantBuffer {
    public final String name;
    public final List<Variable> variables;
    public final EnumSet<D3DShaderCBufferFlags> flags;
    public final D3D11CBufferType type;

    public ConstantBuffer(@NotNull ByteBuffer buffer) {
        final int nameOffset = buffer.getInt();
        final int variableCount = buffer.getInt();
        final int variableDescOffset = buffer.getInt();
        final int bufferSize = buffer.getInt();
        flags = IOUtils.getEnumSet(D3DShaderCBufferFlags.class, buffer.getInt());
        type = IOUtils.getEnum(D3D11CBufferType.class, buffer.getInt());
        buffer.position(nameOffset);
        name = BufferUtils.getString(buffer);
        buffer.position(variableDescOffset);
        variables = new ArrayList<>(variableCount);
        for (int i = 0; i < variableCount; i++) {
            variables.add(new Variable(buffer));
        }
    }


    public String buildVariableLookupFromRegisterId(Index registerId) {
        final String result = buildVariableLookup(registerId);
        final String tmp = buildVariableLookup(registerId);
        return result;
    }

    private String buildVariableLookup(Index registerId) {
        final int offset = (int) (registerId.index * 16);
        final String relativeTo = switch (registerId) {
            case Index.Immediate32PRelativeIndex i -> i.relativeTo.toString();
            case Index.RelativeIndex i -> i.relativeTo.toString();
            default -> null;
        };
        int start = 0;

        final Deque<String> path = new ArrayDeque<>();

        for (Variable variable : variables) {
            if (start <= offset && offset < start + variable.size) {
                if (variable.type.matches(path, start, offset)) {
                    if (relativeTo != null) {
                        path.offerFirst(variable.name + "[" + relativeTo + "]");
                    } else {
                        path.offerFirst(variable.name);
                    }
                    StringBuilder ss = new StringBuilder();
                    int i = 0;
                    for (String s : path) {
                        if (!s.startsWith("[") && i != 0)
                            ss.append('.');
                        ss.append(s);
                        i++;
                    }
                    return ss.toString();
                }
            }

            start += variable.size;
        }

        return null;
    }

}
