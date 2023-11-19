package com.red.dxbc.chunks.xsgn;

import com.red.dxbc.Chunk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class Signature extends Chunk {
    protected static final int ELEMENT_SIZE = 6;

    public final List<D3D11SignatureParameter> parameters;

    public Signature(String name, byte[] data) {
        super(name);
        final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        final int count = buffer.getInt();
        buffer.getInt();
        parameters = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            parameters.add(new D3D11SignatureParameter(buffer, ELEMENT_SIZE));
        }

    }
    public D3D11SignatureParameter getByRegister(int register){
        for (D3D11SignatureParameter parameter : parameters) {
            if(parameter.register==register){
                return parameter;
            }
        }
        return null;
    }

}
