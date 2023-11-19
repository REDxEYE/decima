package com.red.dxbc.chunks.xsgn;

public class InputSignature extends Signature{
    protected static final int ELEMENT_SIZE = 7;
    public InputSignature(String name, byte[] data) {
        super(name, data);
    }
}
