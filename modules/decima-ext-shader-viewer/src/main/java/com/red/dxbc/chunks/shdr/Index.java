package com.red.dxbc.chunks.shdr;

import com.red.dxbc.DXBC;
import com.shade.util.NotNull;

import java.nio.ByteBuffer;

public class Index {
    public final long index;

    public Index(long index) {
        this.index = index;
    }

    public String formattedRepr(DXBC shader) {
        return toString();
    }

    public static class Immediate32Index extends Index {

        public Immediate32Index(@NotNull ByteBuffer buffer) {
            super(buffer.getInt());
        }

        @Override
        public String toString() {
            return Long.toString(this.index);
        }
    }

    public static class Immediate32PRelativeIndex extends Index {
        public final OperandToken0 relativeTo;

        public Immediate32PRelativeIndex(@NotNull ByteBuffer buffer) {
            super(buffer.getInt());
            relativeTo = new OperandToken0(buffer);
        }

        @Override
        public String toString() {
            return "%s + %d".formatted(relativeTo, index);
        }

        @Override
        public String formattedRepr(DXBC shader) {
            return "%s + %d".formatted(relativeTo.toExpression(shader), index);
        }
    }

    public static class Immediate64Index extends Index {
        public Immediate64Index(@NotNull ByteBuffer buffer) {
            super(buffer.getLong());
        }
    }

    public static class RelativeIndex extends Index {
        public final OperandToken0 relativeTo;

        public RelativeIndex(@NotNull ByteBuffer buffer) {
            super(0);
            relativeTo = new OperandToken0(buffer);
        }

        @Override
        public String toString() {
            return "%s".formatted(relativeTo);
        }

        @Override
        public String formattedRepr(DXBC shader) {
            return relativeTo.toExpression(shader).toString();
        }
    }
}
