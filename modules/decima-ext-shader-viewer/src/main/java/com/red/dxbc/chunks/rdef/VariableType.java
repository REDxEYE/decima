package com.red.dxbc.chunks.rdef;

import com.red.dxbc.DXBC;
import com.red.dxbc.chunks.shdr.Index;
import com.shade.platform.model.util.BufferUtils;
import com.shade.platform.model.util.IOUtils;
import com.shade.util.NotImplementedException;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class VariableType {
    public final String name;
    public final D3DShaderVariableClass cls;
    public final D3DShaderVariableType type;
    public final int rows;
    public final int columns;
    public final int elements;
    public final List<Member> members;

    public VariableType(@NotNull ByteBuffer buffer) {
        cls = IOUtils.getEnum(D3DShaderVariableClass.class, buffer.getShort());
        type = IOUtils.getEnum(D3DShaderVariableType.class, buffer.getShort());
        rows = buffer.getShort();
        columns = buffer.getShort();
        elements = buffer.getShort();
        final int memberCount = buffer.getShort();
        final int memberOffset = buffer.getInt();

        buffer.position(buffer.position() + 16);
        final int typeNameOffset = buffer.getInt();
        members = new ArrayList<>(memberCount);
        final int tmp = buffer.position();
        buffer.position(typeNameOffset);
        name = BufferUtils.getString(buffer);
        buffer.position(memberOffset);
        for (int i = 0; i < memberCount; i++) {
            members.add(new Member(buffer));
        }
        buffer.position(tmp);
    }

    public boolean isStruct() {
        return cls == D3DShaderVariableClass.STRUCT;
    }

    public boolean isScalar() {
        return cls == D3DShaderVariableClass.SCALAR ||
            cls == D3DShaderVariableClass.VECTOR;
    }

    public boolean isMatrix() {
        return cls == D3DShaderVariableClass.MATRIX_COLUMNS ||
            cls == D3DShaderVariableClass.MATRIX_ROWS;
    }

    public boolean isColMajorMatrix() {
        return cls == D3DShaderVariableClass.MATRIX_COLUMNS;
    }

    public boolean isRowMajorMatrix() {
        return cls == D3DShaderVariableClass.MATRIX_ROWS;
    }

    public boolean isArray() {
        return elements > 0;
    }

    private int size() {
        return columns * rows * (elements > 0 ? elements : 1) * 4;
    }

    boolean matches(@NotNull Deque<String> path, int memberStart, int offset) {
        for (int i = 0; i < Math.max(elements, 1); i++) {
            if (cls == D3DShaderVariableClass.STRUCT) {
                for (Member member : members) {
                    if (memberStart <= offset && offset < memberStart + member.size()) {
                        if (member.type.matches(path, memberStart, offset)) {
                            if (elements > 0) {
                                path.offerFirst(member.name + "[" + i + "]");
                            } else {
                                path.offerFirst(member.name);
                            }

                            return true;
                        }
                    }

                    memberStart += member.size();
                }
            } else if (cls == D3DShaderVariableClass.VECTOR) {
                if (elements > 0) {
                    final int x = (offset - memberStart) / (columns * 4);
                    if (x > elements) {
                        throw new IllegalArgumentException("Fail");
                    }
                    path.offerFirst("[" + x + "]");
                    return true;
                }
                return memberStart == offset;
            } else if (cls == D3DShaderVariableClass.SCALAR) {
                return memberStart == offset;
            } else if (cls == D3DShaderVariableClass.MATRIX_COLUMNS) {
                final long relativeOffset = offset - memberStart;
                final long x = relativeOffset / (columns * 4L);
                final long y = relativeOffset % (rows * 4L);
                if (y == 0)
                    path.offerFirst("[%d]".formatted(x));
                else
                    path.offerFirst("[%d][%d]".formatted(x, y));
                return true;
            } else if (cls == D3DShaderVariableClass.MATRIX_ROWS) {
                final long relativeOffset = offset - memberStart;
                final long x = relativeOffset / (rows * 4L);
                final long y = relativeOffset % (columns * 4L);
                if (y == 0)
                    path.offerFirst("[%d]".formatted(x));
                else
                    path.offerFirst("[%d][%d]".formatted(x, y));
                return true;
            } else {
                System.out.println(cls);
                throw new IllegalStateException();
            }
        }

        return memberStart == offset;
    }

    public static class Member {
        public final String name;
        public final VariableType type;
        public final int offset;

        public Member(@NotNull String name, @NotNull VariableType type, int offset) {
            this.name = name;
            this.type = type;
            this.offset = offset;
        }

        public Member(@NotNull ByteBuffer buffer) {
            final int nameOffset = buffer.getInt();
            final int typeOffset = buffer.getInt();
            offset = buffer.getInt();
            final int tmp = buffer.position();
            buffer.position(nameOffset);
            name = BufferUtils.getString(buffer);
            buffer.position(typeOffset);
            type = new VariableType(buffer);
            buffer.position(tmp);
        }

        public int size() {
            return type.size();
        }

        public String variableFromOffset(DXBC shader, Index index, long memberOffset) {
            final long registerOffset = index.index * 16;
            if (type.isScalar()) {
                if (registerOffset != offset) {
                    throw new IllegalArgumentException("Offset does not match");
                }
                return "." + name;
            }
            if (type.isArray()) {
                if (index instanceof Index.RelativeIndex relativeIndex) {
                    return name + "[%d]".formatted(relativeIndex.relativeTo) + type;
                }
                throw new NotImplementedException();
            }
            if (type.isMatrix()) {
                if (type.isColMajorMatrix()) {
                    final long x = registerOffset / (type.columns * 4L);
                    final long y = registerOffset % (type.rows * 4L);
                    if (y == 0) {
                        return "." + name + "[%d]".formatted(x);
                    }
                    return "." + name + "[%d][%d]".formatted(x, y);
                }
                if (type.isRowMajorMatrix()) {
                    final long x = registerOffset / (type.rows * 4L);
                    final long y = registerOffset % (type.columns * 4L);
                    if (y == 0) {
                        return "." + name + "[%d]".formatted(x);
                    }
                    return "." + name + "[%d][%d]".formatted(x, y);
                }
            }
            if (type.isStruct() && registerOffset >= offset && registerOffset < offset + size()) {
                if (type.isStruct())
                    for (Member member : type.members) {
                        if (registerOffset >= member.offset && registerOffset < member.offset + member.size())
                            return "." + name + member.variableFromOffset(shader, index, 0);
                    }
            }
            throw new IllegalStateException("Should never reach");
            // return name + type.variableFromOffset(shader, index, offset);
        }
    }
}
