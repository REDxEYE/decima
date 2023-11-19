package com.red.dxbc.decompiler;

public class ImmediateFloat64Value implements Element {
    public final double[] data;

    public ImmediateFloat64Value(double[] data) {
        this.data = data;
    }

    public ImmediateFloat64Value(double data) {
        this.data = new double[1];
        this.data[0] = data;
    }

    @Override
    public String toString() {
        if (data.length == 1) {
            return Double.toString(data[0]);
        }
        StringBuilder ss = new StringBuilder("dvec%d(".formatted(data.length));
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
