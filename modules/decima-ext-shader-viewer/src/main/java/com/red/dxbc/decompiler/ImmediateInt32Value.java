package com.red.dxbc.decompiler;

public class ImmediateInt32Value implements Element {
    public final int[] data;

    public ImmediateInt32Value(int[] data) {
        this.data = data;
    }

    public ImmediateInt32Value(int data) {
        this.data = new int[1];
        this.data[0] = data;
    }

    @Override
    public String toString() {
        if (data.length == 1) {
            return Integer.toString(data[0]);
        }
        StringBuilder ss = new StringBuilder("ivec%d(".formatted(data.length));
        for (int i = 0; i < data.length; i++) {
            ss.append(data[i] & 0xFFFFFFFFL);
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
