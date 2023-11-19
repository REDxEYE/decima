package com.red.dxbc.decompiler;

public class ImmediateFloat32Value implements Element {
    public final float[] data;

    public ImmediateFloat32Value(float[] data) {
        this.data = data;
    }

    public ImmediateFloat32Value(float data) {
        this.data = new float[1];
        this.data[0] = data;
    }

    @Override
    public String toString() {
        if (data.length == 1) {
            return Float.toString(data[0]);
        }
        StringBuilder ss = new StringBuilder("vec%d(".formatted(data.length));
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
