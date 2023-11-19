package com.red.dxbc;

import com.shade.platform.model.util.EnumValue;

public enum ComponentMask implements EnumValue {
    NONE,   // 0000
    X,      // 0001
    Y,      // 0010
    XY,     // 0011
    Z,      // 0100
    XZ,     // 0101
    YZ,     // 0110
    XYZ,    // 0111
    W,      // 1000
    XW,     // 1001
    YW,     // 1010
    XYW,    // 1011
    ZW,     // 1100
    XZW,    // 1101
    YZW,    // 1110
    XYZW;   // 1111

    @Override
    public int value() {
        return ordinal();
    }

    public int componetCount() {
        return switch (this) {
            case X, Y, Z, W -> 1;
            case XY, XZ, XW, YZ, YW, ZW -> 2;
            case XYZ, XYW, XZW, YZW -> 3;
            case XYZW -> 4;
            case NONE -> 0;

        };
    }

}
