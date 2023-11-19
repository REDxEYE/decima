package com.shade.platform.model.util;

public class BitUnpacker {
    private final int value;
    private int offset;

    public BitUnpacker(int value) {
        this.value = value;
        this.offset = 0;
    }

    public static int getIntRange(int value, int startBit, int endBit) {
        if (startBit > endBit || startBit < 0 || endBit > 31) {
            throw new IllegalArgumentException("Invalid bit range");
        }
        int mask = ((1 << (endBit - startBit + 1)) - 1) << startBit;
        return (value & mask) >>> startBit;
    }

    public static int getInt(int value, int bitOffset, int bitSize) {
        if (bitOffset < 0 || bitSize <= 0 || bitOffset + bitSize > 32) {
            throw new IllegalArgumentException("Invalid bit offset or size");
        }
        return (value >>> bitOffset) & ((1 << bitSize) - 1);
    }


    public static boolean getBool(int value, int bitOffset) {
        return getInt(value, bitOffset, 1) == 1;
    }

    public int getInt(int bitOffset, int bitSize) {
        return getInt(value, bitOffset, bitSize);
    }

    public int getInt(int bitSize) {
        final int val = getInt(value, offset, bitSize);
        offset += bitSize;
        return val;
    }

    public void skip(int bitSize) {
        offset += bitSize;
    }

    public boolean getBool(int bitOffset) {
        return getBool(value, bitOffset);
    }

    public boolean getBool() {
        final boolean val = getBool(value, offset);
        offset++;
        return val;
    }
}
