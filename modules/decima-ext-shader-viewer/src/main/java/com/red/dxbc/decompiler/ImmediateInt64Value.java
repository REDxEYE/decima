package com.red.dxbc.decompiler;

public class ImmediateInt64Value implements Element {
    public final long[] data;

    public ImmediateInt64Value(long[] data) {
        this.data = data;
    }

    public ImmediateInt64Value(long data) {
        this.data = new long[1];
        this.data[0] = data;
    }

    @Override
    public String toString() {
        if (data.length == 1) {
            return Long.toString(data[0]);
        }
        StringBuilder ss = new StringBuilder("lvec%d(".formatted(data.length));
        for (int i = 0; i < data.length; i++) {
            ss.append(data[i]);
            if (i != data.length - 1)
                ss.append(", ");

        }
        ss.append(')');
        return ss.toString();
    }

    @Override
    public int getOperandUseCount(Operand operand) {
        return 0;
    }
}
