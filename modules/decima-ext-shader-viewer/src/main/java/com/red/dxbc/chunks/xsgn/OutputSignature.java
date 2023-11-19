package com.red.dxbc.chunks.xsgn;

public class OutputSignature extends Signature {
    protected static final int ELEMENT_SIZE = 7;

    public OutputSignature(String name, byte[] data) {
        super(name, data);
    }
}
